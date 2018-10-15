package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Mail;
import com.dazito.oauthexample.service.MailService;
import com.dazito.oauthexample.service.OAuth2Service;
import com.dazito.oauthexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.bind.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//import com.dazito.oauthexample.utils.email_sender.MessageSender;
//import com.dazito.oauthexample.utils.email_sender.email.EmailMessageSender;

@Service
public class OAuth2ServiceImpl implements OAuth2Service {

    @Resource(name = "defaultTokenService")
    private DefaultTokenServices tokenServices;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    UserService userService;
    @Resource
    TokenStore tokenStore;
    @Autowired
    MailService mailService;

    public void deleteToken(AccountEntity account) {
        getAllBeans();
        ClientRegistrationService clientDetailsService = applicationContext.getBean(ClientRegistrationService.class);
        (clientDetailsService).listClientDetails().stream()
                .map(ClientDetails::getClientId)
                .map(client -> tokenStore.findTokensByClientIdAndUserName(client, account.getEmail()))
                .flatMap(Collection::stream)
                .map(OAuth2AccessToken::getValue)
                .forEach(tokenServices::revokeToken);
    }

    @Override
    public void messageReply(String uuid, String email) {

    }

    private void getAllBeans() {
        System.out.println("///////////////////LIST BEANS////////////////////////");
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            System.out.println(beanName + " : " + applicationContext.getBean(beanName) + "");
        }
        System.out.println("//////////////////////////////////////////////////////////");
    }
}
