package com.dazito.oauthexample.config.oauth;

import com.dazito.oauthexample.config.AppConfig;
import com.dazito.oauthexample.service.impl.AccountUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@EnableAuthorizationServer
@Configuration
public class AuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {


    private final AuthenticationManager authenticationManager;
    private final AppConfig appConfig;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountUserDetailsService userDetailsService;
    private final TokenStore tokenStore;

    @Autowired
    public AuthServerOAuth2Config(AuthenticationManager authenticationManager, AppConfig appConfig,
                                  BCryptPasswordEncoder passwordEncoder, AccountUserDetailsService userDetailsService,
                                  TokenStore tokenStore) {
        this.authenticationManager = authenticationManager;
        this.appConfig = appConfig;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.tokenStore = tokenStore;
    }
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // Connect to BD
        clients.jdbc(appConfig.dataSource()). passwordEncoder(passwordEncoder);
    }
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // Spring Security OAuth exposes two endpoints for checking tokens (/oauth/check_token and /oauth/token_key).
        // Those endpoints are not exposed by default (have access "denyAll()").
        security.passwordEncoder(passwordEncoder)
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()");
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // The AuthenticationManager for the password grant.
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                // Persist the tokens in the database
                .tokenStore(tokenStore)
                .approvalStoreDisabled();
    }
}
