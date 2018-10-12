package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;

public interface OAuth2Service {

    void deleteToken(AccountEntity account);

    void method(AccountEntity user);
}
