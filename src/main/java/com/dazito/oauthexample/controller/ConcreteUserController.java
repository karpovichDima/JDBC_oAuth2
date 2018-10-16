package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.EditPersonalDataDto;
import com.dazito.oauthexample.service.dto.request.SetPasswordDto;
import com.dazito.oauthexample.service.dto.response.ChangedActivateDto;
import com.dazito.oauthexample.service.dto.response.DeletedUserDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.service.dto.response.EditedPasswordDto;
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
    public ResponseEntity<AccountDto> getAccountCurrentUser(@PathVariable Long id) {
        AccountEntity foundedUser = userService.findByIdAccountRepo(id);
        AccountDto accountDto = userService.addToAccountDtoOrganization(foundedUser);
        return ResponseEntity.ok(accountDto);
    }

    // edit email and name of the current user
    @PatchMapping("/{id}")
    public ResponseEntity<EditedEmailNameDto> editEmail(@PathVariable Long id,
                                                        @RequestBody EditPersonalDataDto editPersonalDataDto) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException, UserWithSuchEmailExistException {
        EditedEmailNameDto editEmail = userService.editPersonData(id, editPersonalDataDto);
        return ResponseEntity.ok(editEmail);
    }

    // edit password of the user by id
    @PatchMapping("/password/{id}")
    public ResponseEntity<EditedPasswordDto> editPassword(@PathVariable Long id,
                                                          @RequestBody EditPersonalDataDto editPersonalDataDto) throws CurrentUserIsNotAdminException, PasswordNotMatchesException, OrganizationIsNotMuchException, EmptyFieldException {
        EditedPasswordDto editedPasswordDto = userService.editPassword(id, editPersonalDataDto.getNewPassword(), editPersonalDataDto.getRawOldPassword());
        return ResponseEntity.ok(editedPasswordDto);
    }

    // delete user by email from accountDto
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedUserDto> deleteUser(@PathVariable Long id) throws CurrentUserIsNotAdminException, PasswordNotMatchesException, OrganizationIsNotMuchException, EmailIsNotMatchesException {
        DeletedUserDto accountDto = userService.deleteUser(id, null);
        return ResponseEntity.ok(accountDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/activated")
    public ResponseEntity<ChangedActivateDto> editActivate(@RequestBody AccountDto accountDto) throws OrganizationIsNotMuchException, CurrentUserIsNotAdminException {
        ChangedActivateDto changedActivateDto = userService.editActivate(accountDto);
        return ResponseEntity.ok(changedActivateDto);
    }











    // create new user from accountDto
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/")
    public ResponseEntity<EditedEmailNameDto> createUser(@RequestBody AccountDto accountDto) throws ValidationException, OrganizationIsNotMuchException, CurrentUserIsNotAdminException {
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(newUser);
    }

    // create new user from accountDto without password
    @PostMapping("/create")
    public ResponseEntity<EditedEmailNameDto> requestToCreateUser(@RequestBody AccountDto accountDto) throws ValidationException, OrganizationIsNotMuchException, CurrentUserIsNotAdminException {
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(newUser);
    }



    @PatchMapping("/password")
    public ResponseEntity<SetPasswordDto> setNewPasswordAfterCreateUser(@RequestBody SetPasswordDto setPasswordDto) {
        userService.messageReply(setPasswordDto);
        return ResponseEntity.ok(setPasswordDto);
    }
}
