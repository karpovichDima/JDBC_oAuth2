package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.config.oauth.CustomUserDetails;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DtoForEditingPersonalData;
import com.dazito.oauthexample.service.dto.response.EmailNameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/{id}")
public class ConcreteUserController {

    @Autowired
    private UserService userService;

    // get user by id
    @GetMapping()
    public AccountDto getAccountCurrentUser(@PathVariable Long id) {
        AccountEntity foundedUser = userService.findById(id);
        return userService.addToAccountDtoOrganization(foundedUser);
    }

    // edit email and name of the current user
    @PatchMapping("/")
    public ResponseEntity<EmailNameDto> editEmail(@PathVariable Long id, @RequestBody DtoForEditingPersonalData dtoForEditingPersonalData) {
        EmailNameDto editEmail = userService.editPersonData(id, dtoForEditingPersonalData);
        return ResponseEntity.ok(editEmail);
    }

    // edit password of the user by id
    @PatchMapping("/password")
    public ResponseEntity<PasswordDto> editPassword(@PathVariable Long id, @RequestBody DtoForEditingPersonalData dtoForEditingPersonalData) {
        PasswordDto passwordDto = userService.editPassword(id, dtoForEditingPersonalData.getNewPassword(), dtoForEditingPersonalData.getRawOldPassword());
        return ResponseEntity.ok(passwordDto);
    }

    // get id of the user by id
    private Long findOutIdUser(){
        return ((CustomUserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser().getId();
    }

}