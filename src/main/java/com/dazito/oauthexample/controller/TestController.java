package com.dazito.oauthexample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(path = "/tests")
public class TestController {

    @RequestMapping(method = RequestMethod.GET)
    public String test() {
        return "Hello";
    }
}
