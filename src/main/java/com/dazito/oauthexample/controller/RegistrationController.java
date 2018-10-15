package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.ContentService;
import com.dazito.oauthexample.service.MailService;
import com.dazito.oauthexample.service.OAuth2Service;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.io.IOException;

@RestController
@RequestMapping(path = "/registration")
public class RegistrationController {

    @Autowired
    UserService userService;

    @Autowired
    ContentService contentService;

    @Autowired
    OAuth2Service oAuth2Service;

    @Autowired
    MailService mailService;

    // create new user from accountDto without password
    @PostMapping("/")
    public ResponseEntity<EditedEmailNameDto> registration(@RequestBody AccountDto accountDto) throws ValidationException {
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/{uuid}/{id}")
    public ResponseEntity<String> messageArrived(@PathVariable String uuid, @PathVariable String id) throws IOException {
        oAuth2Service.messageReply(uuid, id);
        return ResponseEntity.ok(uuid);
    }

}
