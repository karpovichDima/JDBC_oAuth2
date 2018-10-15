package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.dto.request.SetPasswordDto;

import javax.xml.bind.ValidationException;

public interface OAuth2Service {

    void deleteToken(AccountEntity account);
}
