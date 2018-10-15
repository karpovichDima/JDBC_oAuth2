package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.ContentService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.response.ChangedActivateDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ContentService contentService;


    // create new user from accountDto
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/")
    public ResponseEntity<EditedEmailNameDto> createUser(@RequestBody AccountDto accountDto) throws ValidationException {
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(newUser);
    }


}
