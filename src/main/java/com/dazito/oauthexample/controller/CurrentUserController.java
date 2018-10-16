package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DeleteAccountDto;
import com.dazito.oauthexample.service.dto.request.EditPersonalDataDto;
import com.dazito.oauthexample.service.dto.response.ContentUpdatedDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.service.dto.response.EditedPasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/current")
public class CurrentUserController {

    @Autowired
    UserService userService;

    @GetMapping()
    public AccountDto getAccountCurrentUser() {
        AccountEntity foundedUser = userService.getCurrentUser();
        return userService.addToAccountDtoOrganization(foundedUser);
    }

    // edit email and name of the current user
    @PatchMapping("/")
    public ResponseEntity<EditedEmailNameDto> editNameEmail(@RequestBody EditPersonalDataDto editPersonalDataDto) {
        EditedEmailNameDto editEmail = userService.editPersonData(null, editPersonalDataDto);
        return ResponseEntity.ok(editEmail);
    }

    // edit password of the current user
    @PatchMapping("/password")
    public ResponseEntity<EditedPasswordDto> editPassword(@RequestBody EditPersonalDataDto editingPersonalData) {
        EditedPasswordDto editedPasswordDto = userService.editPassword(null, editingPersonalData.getNewPassword(),editingPersonalData.getRawOldPassword());
        return ResponseEntity.ok(editedPasswordDto);
    }

    @DeleteMapping("/")
    public ResponseEntity<AccountDto> deleteUser(@RequestBody DeleteAccountDto accountDto){
        AccountDto result = userService.deleteUser(null, accountDto);
        return ResponseEntity.ok(result);
    }

}
