package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.UserRepository;
import com.dazito.oauthexample.model.Account;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.response.NameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import com.dazito.oauthexample.utils.ConverterAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServicesImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServicesImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public AccountDto getCurrentUser(String name) {
        return ConverterAccount.mapAccountEntityToDto(findUserByName(name));
    }

    @Override
    public PasswordDto editPassword(String name, String newPassword, String rawOldPassword) {
        Account account = findUserByName(name);
        boolean matches = passwordEncoder.matches(rawOldPassword, account.getPassword());
        if (matches){
            account.setPassword(passwordEncoder.encode(newPassword));
            userRepository.saveAndFlush(account);
        }

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setPassword(newPassword);
        return passwordDto;
    }

    @Override
    public NameDto editName(String name, String newName) {
        Account account = findUserByName(name);
        account.setUsername(newName);
        userRepository.saveAndFlush(account);

        NameDto nameDto = new NameDto();
        nameDto.setName(newName);
        return nameDto;
    }

    private Account findUserByName(String name){
        if (userRepository.findByUsername(name).isPresent()){
            return  userRepository.findByUsername(name).get();
        } else {
            return new Account();
        }
    }
}
