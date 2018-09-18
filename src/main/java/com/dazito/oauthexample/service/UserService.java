package com.dazito.oauthexample.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * edit password of the current user
     * @param name is name user, whose password we edit
     */
    void editPassword(String name, String newPassword);

    /**
     * edit name of the current user
     * @param newName is the user name to which the user changes their name
     */
    void editName(String name, String newName);
}
