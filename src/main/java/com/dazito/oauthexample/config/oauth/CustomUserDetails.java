package com.dazito.oauthexample.config.oauth;

import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import javassist.SerialVersionUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails, Serializable{
    private AccountDto user;

    @Autowired
    AccountRepository accountRepository;

    public CustomUserDetails(AccountDto user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> collection = new ArrayList<>(1);
        collection.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        Collection<SimpleGrantedAuthority> collection1 = collection;
        return collection1;
    }

    @Override
    public boolean isEnabled() {
        Boolean activated = user.getIsActivated();
        return activated;
    }

    @Override
    public String getUsername() {
        String email = user.getEmail();
        return email;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String getPassword() {
        String password = user.getPassword();
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    public AccountDto getUser() {
        return this.user;
    }

}
