package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.MailService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.SetPasswordDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping(path = "/registration")
public class RegistrationController {

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    // create new user from accountDto without password
    @PostMapping("/")
    public ResponseEntity<EditedEmailNameDto> registration(@RequestBody AccountDto accountDto) throws ValidationException {
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/password/create")
    public ResponseEntity<SetPasswordDto> setNewPasswordAfterCreateUser(@RequestBody SetPasswordDto setPasswordDto) {
        userService.messageReply(setPasswordDto);
        return ResponseEntity.ok(setPasswordDto);
    }

    @PostMapping("/password/recovery")
    public ResponseEntity<SetPasswordDto> setNewPasswordAfterForgot(@RequestBody SetPasswordDto setPasswordDto) {
        userService.forgotPassword(setPasswordDto);
        return ResponseEntity.ok(setPasswordDto);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<SetPasswordDto> sendEmailForgotPassword(@RequestBody SetPasswordDto setPasswordDto) throws ValidationException {
        mailService.emailPreparation(setPasswordDto.getEmail());
        return ResponseEntity.ok(setPasswordDto);
    }

}
