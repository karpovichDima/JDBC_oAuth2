package com.dazito.oauthexample.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @GetMapping("/current")
    public String getInfoAboutUser() {
        return String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

}
