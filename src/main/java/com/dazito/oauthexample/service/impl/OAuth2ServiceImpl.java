package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

@Service
public class OAuth2ServiceImpl implements OAuth2Service {

    @Resource(name = "defaultTokenService")
    private DefaultTokenServices tokenServices;

    @Autowired
    ApplicationContext applicationContext;
//    @Resource
    ClientRegistrationService clientDetailsService;
    @Resource
    TokenStore tokenStore;


    public void deleteToken(AccountEntity account) {
        method(account);
//        defaultTokenServices.revokeToken(principal.toString());
    }

    public void method(AccountEntity user) {
        clientDetailsService = applicationContext.getBean(ClientRegistrationService.class);
//        List<ClientDetails> clientDetails =



        (this.clientDetailsService).listClientDetails().stream()
                .map(ClientDetails::getClientId)
                .map(client -> tokenStore.findTokensByClientIdAndUserName(client, user.getEmail()))
                .flatMap(Collection::stream)
                .map(OAuth2AccessToken::getValue)
                .forEach(tokenServices::revokeToken);

    }


}
