package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.UserRepositoryDAO;
import com.dazito.oauthexample.entities.Account;
import com.dazito.oauthexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServicesImpl implements UserService {

    private final UserRepositoryDAO userRepositoryDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServicesImpl(UserRepositoryDAO userRepositoryDAO, PasswordEncoder passwordEncoder) {
        this.userRepositoryDAO = userRepositoryDAO;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void editPassword(String name, String newPassword) {
        Account account = userRepositoryDAO.findByUsername(name).get();
        account.setPassword(passwordEncoder.encode(newPassword));
        userRepositoryDAO.saveAndFlush(account);
    }

    @Override
    public void editName(String name, String newName) {
        Account account = userRepositoryDAO.findByUsername(name).get();
        account.setUsername(newName);
        userRepositoryDAO.saveAndFlush(account);
    }
}
