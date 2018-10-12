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
    public ResponseEntity<EditedEmailNameDto> createUser(@RequestBody AccountDto accountDto){
        EditedEmailNameDto newUser = userService.createUser(accountDto, true);
        return ResponseEntity.ok(newUser);
    }

    // create new user from accountDto without password
    @PostMapping("/registration")
    public ResponseEntity<EditedEmailNameDto> registration(@RequestBody AccountDto accountDto){
        EditedEmailNameDto newUser = userService.createUser(accountDto, false);
        return ResponseEntity.ok(newUser);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/activated")
    public ResponseEntity<ChangedActivateDto> editActivate(@RequestBody AccountDto accountDto) {
        ChangedActivateDto changedActivateDto = userService.editActivate(accountDto);
        return ResponseEntity.ok(changedActivateDto);
    }
}
