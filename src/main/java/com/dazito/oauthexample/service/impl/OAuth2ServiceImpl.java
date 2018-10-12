package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
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
    ClientRegistrationService clientDetailsService;
    @Resource
    TokenStore tokenStore;


    public void deleteToken(AccountEntity account) {
        getAllBeans();
        clientDetailsService = applicationContext.getBean(ClientRegistrationService.class);
        (this.clientDetailsService).listClientDetails().stream()
                .map(ClientDetails::getClientId)
                .map(client -> tokenStore.findTokensByClientIdAndUserName(client, account.getEmail()))
                .flatMap(Collection::stream)
                .map(OAuth2AccessToken::getValue)
                .forEach(tokenServices::revokeToken);
    }

    void getAllBeans() {
        System.out.println("//////////////////////////////////////////////////////////");
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            System.out.println(beanName + " : " + applicationContext.getBean(beanName).getClass().toString());
        }
        System.out.println("//////////////////////////////////////////////////////////");
    }
}
