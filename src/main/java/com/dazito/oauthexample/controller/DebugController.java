package com.dazito.oauthexample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DebugController {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/hello")
    public String hello(@RequestParam(value="key", required=false, defaultValue="ClientRegistrationService") String name, Model model) {


        model.addAttribute("name", name);
        return "helloworld";
    }
}
