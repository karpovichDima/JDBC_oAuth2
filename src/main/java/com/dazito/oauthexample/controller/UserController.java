package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/current")
    public String getInfoAboutUser() {
        return String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @PatchMapping("/password")
    public void editPassword() {
        userService.editPassword("dima");
    }

    @PatchMapping("/name")
    public String editNames() {
        return String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }




}
