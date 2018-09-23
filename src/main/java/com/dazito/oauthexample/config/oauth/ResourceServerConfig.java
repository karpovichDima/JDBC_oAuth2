package com.dazito.oauthexample.config.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

@Configuration
@EnableResourceServer
class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(ResourceServerConfig.class);

    @Autowired
    RestLogoutSuccessHandler restLogoutSuccessHandler;

    @Resource(name = "tokenStore")
    private TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) {
        try {
            // Prescribe the roles that our paths are available
            http
                    .authorizeRequests()
                    .antMatchers("/users/*").access("hasRole('USER')")
                    .antMatchers("/admins/*").access("hasRole('ADMIN')")
                    .anyRequest().hasRole("USER")
                    // all it for logout
                    .and()
                    .logout().logoutUrl("/logout")
                    // config for logout
                    .logoutSuccessHandler(restLogoutSuccessHandler)
                    .clearAuthentication(true)
                    .permitAll()
                    .and()
                    .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());

        } catch (Exception e) {
            logger.error("HttpSecurity configuration failed", e);
        }
    }
}
