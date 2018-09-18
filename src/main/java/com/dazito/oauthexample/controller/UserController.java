package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    // get current user
    @GetMapping("/current")
    public String getInfoAboutUser() {
        return String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    // edit password of the current user
    @PatchMapping("/password")
    public void editPassword() {
        userService.editPassword(findOutNameUser(), "qwerty");
    }

    // edit name of the current user
    @PatchMapping("/name")
    public String editNames() {
        return String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }


    // get name of the current user
    private String findOutNameUser(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
