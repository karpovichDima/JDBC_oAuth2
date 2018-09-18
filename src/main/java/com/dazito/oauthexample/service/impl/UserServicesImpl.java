package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.entities.Account;
import com.dazito.oauthexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServicesImpl implements UserService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void editPassword(String name, String newPassword) {
        Account account = accountRepository.findByUsername(name).get();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.saveAndFlush(account);
    }
}
