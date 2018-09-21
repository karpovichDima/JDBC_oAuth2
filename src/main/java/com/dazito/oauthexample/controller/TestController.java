package com.dazito.oauthexample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/tests")
public class TestController {

    @GetMapping
    public String test() {
        return "Hello";
    }



}
