package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.MailService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.DeleteAccountDto;
import com.dazito.oauthexample.service.dto.request.EditPersonalDataDto;
import com.dazito.oauthexample.service.dto.request.SetPasswordDto;
import com.dazito.oauthexample.service.dto.response.DeletedUserDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.service.dto.response.EditedPasswordDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping(path = "/users/current")
public class CurrentUserController {

    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;

    @GetMapping()
    public ResponseEntity<GeneralResponseDto<AccountEntity>> getAccountCurrentUser() {
        AccountEntity foundedUser = userService.getCurrentUser();
        return ResponseEntity.ok(new GeneralResponseDto<>(null, foundedUser));
    }

    // edit email and name of the current user
    @PatchMapping("/")
    public ResponseEntity<GeneralResponseDto<EditedEmailNameDto>> editNameEmail(@RequestBody EditPersonalDataDto editPersonalDataDto) throws AppException {
        EditedEmailNameDto editEmail = userService.editPersonData(null, editPersonalDataDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, editEmail));
    }

    // edit password of the current user
    @PatchMapping("/password")
    public ResponseEntity<GeneralResponseDto<EditedPasswordDto>> editPassword(@RequestBody EditPersonalDataDto editingPersonalData) throws AppException {
        EditedPasswordDto editedPasswordDto = userService.editPassword(null, editingPersonalData.getNewPassword(),editingPersonalData.getRawOldPassword());
        return ResponseEntity.ok(new GeneralResponseDto<>(null, editedPasswordDto));
    }

    @DeleteMapping("/")
    public ResponseEntity<GeneralResponseDto<DeletedUserDto>> deleteUser(@RequestBody DeleteAccountDto accountDto) throws AppException {
        DeletedUserDto result = userService.deleteUser(null, accountDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, result));

    }

    @PostMapping("/recovery")
    public ResponseEntity<GeneralResponseDto<SetPasswordDto>> setNewPasswordAfterForgot(@RequestBody SetPasswordDto setPasswordDto) {
        userService.forgotPassword(setPasswordDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, setPasswordDto));
    }

    @PostMapping("/forgot")
    public ResponseEntity<GeneralResponseDto<SetPasswordDto>> sendEmailForgotPassword(@RequestBody SetPasswordDto setPasswordDto) throws ValidationException {
        mailService.emailPreparation(setPasswordDto.getEmail());
        return ResponseEntity.ok(new GeneralResponseDto<>(null, setPasswordDto));
    }

}
