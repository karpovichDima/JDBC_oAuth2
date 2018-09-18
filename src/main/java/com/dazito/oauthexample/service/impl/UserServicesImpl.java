package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServicesImpl implements UserService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public void editPassword(String name) {
        accountRepository.findByUsername(name);
    }
}
