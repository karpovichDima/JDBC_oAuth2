package com.dazito.oauthexample.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * edit password of the current user
     * @param name is name user, whose password we edit
     */
    void editPassword(String name, String newPassword);
}
