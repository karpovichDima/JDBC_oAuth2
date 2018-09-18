package com.dazito.oauthexample.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(path = "/account")
public class TestController {

    @RequestMapping(method = RequestMethod.GET)
    public String test() {
       String testString = "Hello";
       return testString;
    }


    @RequestMapping(method = RequestMethod.POST)
    public Principal oauth(Principal principal) {
            /*
             * Translate the incoming request, which has an access token
             * Spring security takes the incoming request and injects the Java Security Principal
             * The converter inside Spring Security will handle the to json method which the Spring Security
             * Oauth client will know how to read
             *
             * The @EnableResourceServer on the application entry point is what makes all this magic happen.
             * If ☺there is an incoming request token it will check the token validity and handle it accordingly
             */
    return principal;
    }

}
