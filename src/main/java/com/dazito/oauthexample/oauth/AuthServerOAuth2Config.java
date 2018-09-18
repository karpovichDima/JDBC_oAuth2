package com.dazito.oauthexample.oauth;

import com.dazito.oauthexample.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@EnableAuthorizationServer
@Configuration
public class AuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {


    private final AuthenticationManager authenticationManager;
    private final AppConfig appConfig;
    
    @Autowired
    public AuthServerOAuth2Config(AuthenticationManager authenticationManager, AppConfig appConfig) {
        this.authenticationManager = authenticationManager;
        this.appConfig = appConfig;
    }
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // Connect to BD
        clients.jdbc(appConfig.dataSource());
    }
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // Spring Security OAuth exposes two endpoints for checking tokens (/oauth/check_token and /oauth/token_key).
        // Those endpoints are not exposed by default (have access "denyAll()").
        security.checkTokenAccess("permitAll()");
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // The AuthenticationManager for the password grant.
                .authenticationManager(authenticationManager)
                // Persist the tokens in the database
                .tokenStore(appConfig.tokenStore());
    }
}
