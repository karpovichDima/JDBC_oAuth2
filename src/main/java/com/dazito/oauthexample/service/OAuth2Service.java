package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.dto.request.SetPasswordDto;

import javax.xml.bind.ValidationException;

public interface OAuth2Service {

    /**
     * delete access token by account entity
     * @param account the user entity
     */
    void deleteToken(AccountEntity account);
}
