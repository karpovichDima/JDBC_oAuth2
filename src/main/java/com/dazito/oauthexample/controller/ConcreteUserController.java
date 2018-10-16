package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.EditPersonalDataDto;
import com.dazito.oauthexample.service.dto.response.ChangedActivateDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.service.dto.response.EditedPasswordDto;
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
    @GetMapping()
    public ResponseEntity<AccountDto> getAccountCurrentUser(@PathVariable Long id) {
        AccountEntity foundedUser = userService.findByIdAccountRepo(id);
        AccountDto accountDto = userService.addToAccountDtoOrganization(foundedUser);
        return ResponseEntity.ok(accountDto);
    }

    // edit email and name of the current user
    @PatchMapping("/{id}")
    public ResponseEntity<EditedEmailNameDto> editEmail(@PathVariable Long id, @RequestBody EditPersonalDataDto editPersonalDataDto) {
        EditedEmailNameDto editEmail = userService.editPersonData(id, editPersonalDataDto);
        return ResponseEntity.ok(editEmail);
    }

    // edit password of the user by id
    @PatchMapping("/password/{id}")
    public ResponseEntity<EditedPasswordDto> editPassword(@PathVariable Long id, @RequestBody EditPersonalDataDto editPersonalDataDto) {
        EditedPasswordDto editedPasswordDto = userService.editPassword(id, editPersonalDataDto.getNewPassword(), editPersonalDataDto.getRawOldPassword());
        return ResponseEntity.ok(editedPasswordDto);
    }

    // delete user by email from accountDto
    @DeleteMapping("/{id}")
    public ResponseEntity<AccountDto> deleteUser(@PathVariable Long id){
        AccountDto accountDto = userService.deleteUser(id, null);
        return ResponseEntity.ok(accountDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/activated")
    public ResponseEntity<ChangedActivateDto> editActivate(@RequestBody AccountDto accountDto) {
        ChangedActivateDto changedActivateDto = userService.editActivate(accountDto);
        return ResponseEntity.ok(changedActivateDto);
    }

    // create new user from accountDto
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/")
    public ResponseEntity<EditedEmailNameDto> createUser(@RequestBody AccountDto accountDto) throws ValidationException {
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(newUser);
    }

    // create new user from accountDto without password
    @PostMapping("/")
    public ResponseEntity<EditedEmailNameDto> requestToCreateUser(@RequestBody AccountDto accountDto) throws ValidationException {
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(newUser);
    }

}
