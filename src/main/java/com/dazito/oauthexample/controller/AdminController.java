package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.ContentService;
import com.dazito.oauthexample.service.MailService;
import com.dazito.oauthexample.service.OAuth2Service;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.response.ChangedActivateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/")
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    ContentService contentService;

    @Autowired
    OAuth2Service oAuth2Service;

    @Autowired
    MailService mailService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/activated")
    public ResponseEntity<ChangedActivateDto> editActivate(@RequestBody AccountDto accountDto) {
        ChangedActivateDto changedActivateDto = userService.editActivate(accountDto);
        return ResponseEntity.ok(changedActivateDto);
    }
}
