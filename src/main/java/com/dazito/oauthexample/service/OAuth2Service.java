package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;

import javax.xml.bind.ValidationException;

public interface OAuth2Service {

    void deleteToken(AccountEntity account);

    void messageReply(String uuid, String email);
}
