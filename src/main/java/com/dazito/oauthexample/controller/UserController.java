package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.EditNameDto;
import com.dazito.oauthexample.service.dto.request.EditPasswordDto;
import com.dazito.oauthexample.service.dto.response.NameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    // get current user
    @GetMapping("/current")
    public AccountDto getAccountCurrentUser() {
        return userService.getCurrentUser(findOutNameUser());
    }

    // edit password of the current user
    @PatchMapping("/password")
    public ResponseEntity<PasswordDto> editPassword(@RequestBody EditPasswordDto editPassword) {
        PasswordDto passwordDto = userService.editPassword(findOutNameUser(),editPassword.getNewPassword(),editPassword.getRawOldPassword());
        return ResponseEntity.ok(passwordDto);
    }

    // edit name of the current user
    @PatchMapping("/name")
    public ResponseEntity<NameDto> editNames(@RequestBody EditNameDto editNameDto) {
        NameDto nameDto = userService.editName(findOutNameUser(), editNameDto.getNewName());
        return ResponseEntity.ok(nameDto);
    }

    // get name of the current user
    private String findOutNameUser(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
