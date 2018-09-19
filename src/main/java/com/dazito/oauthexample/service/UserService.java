package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.Account;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.response.NameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    /**
     * get current user
     * @param name is name current user
     */
    AccountDto getCurrentUser(String name);

    /**
     * edit password of the current user
     * @param name is name user, whose password we edit
     */
    PasswordDto editPassword(String name, String newPassword, String rawOldPassword);

    /**
     * edit name of the current user
     * @param newName is the user name to which the user changes their name
     */
    NameDto editName(String name, String newName);


}
