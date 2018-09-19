package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.UserRepositoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {

    private UserRepositoryDAO userRepositoryDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AccountUserDetailsService(UserRepositoryDAO userRepositoryDAO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepositoryDAO = userRepositoryDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepositoryDAO
                .findByUsername(username)
                .map(account -> new User(account.getUsername(), account.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER")))
                .orElseThrow(() -> new UsernameNotFoundException("Could not find " + username));
    }
}
