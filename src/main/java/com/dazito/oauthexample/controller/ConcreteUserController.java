package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.EditPersonalDataDto;
import com.dazito.oauthexample.service.dto.request.SetPasswordDto;
import com.dazito.oauthexample.service.dto.response.*;
import com.dazito.oauthexample.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping(path = "/users")
public class ConcreteUserController {

    private final UserService userService;

    @Autowired
    public ConcreteUserController(UserService userService) {
        this.userService = userService;
    }

    // get user by id
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponseDto<AccountDto>> getAccountCurrentUser(@PathVariable Long id) throws AppException {
        AccountEntity foundedUser = userService.findByIdAccountRepo(id);
        AccountDto accountDto = userService.addToAccountDtoOrganization(foundedUser);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, accountDto));
    }

    // edit email and name of the current user
    @PatchMapping("/{id}")
    public ResponseEntity<GeneralResponseDto<EditedEmailNameDto>> editEmail(@PathVariable Long id,
                                                                            @RequestBody EditPersonalDataDto editPersonalDataDto) throws AppException {
        EditedEmailNameDto editEmail = userService.editPersonData(id, editPersonalDataDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, editEmail));
    }

    // edit password of the user by id
    @PatchMapping("/password/{id}")
    public ResponseEntity<GeneralResponseDto<EditedPasswordDto>> editPassword(@PathVariable Long id,
                                                                              @RequestBody EditPersonalDataDto editPersonalDataDto) throws AppException {
        EditedPasswordDto editedPasswordDto = userService.editPassword(id, editPersonalDataDto.getNewPassword(), editPersonalDataDto.getRawOldPassword());
        return ResponseEntity.ok(new GeneralResponseDto<>(null, editedPasswordDto));
    }

    // delete user by email from accountDto
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponseDto<DeletedUserDto>> deleteUser(@PathVariable Long id) throws AppException {
        DeletedUserDto accountDto = userService.deleteUser(id, null);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, accountDto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/activated")
    public ResponseEntity<GeneralResponseDto<ChangedActivateDto>> editActivate(@RequestBody AccountDto accountDto) throws AppException {
        ChangedActivateDto changedActivateDto = userService.editActivate(accountDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, changedActivateDto));
    }

    // create new user from accountDto without password
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponseDto<EditedEmailNameDto>> createUser(@RequestBody AccountDto accountDto) throws ValidationException, AppException {
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, newUser));
    }

    @PatchMapping("/password")
    public ResponseEntity<GeneralResponseDto<SetPasswordDto>> setNewPasswordAfterCreateUser(@RequestBody SetPasswordDto setPasswordDto) {
        userService.messageReply(setPasswordDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, setPasswordDto));
    }
}
