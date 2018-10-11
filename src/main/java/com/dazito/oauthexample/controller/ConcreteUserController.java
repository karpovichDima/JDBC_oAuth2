package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.config.oauth.UserDetailsConfig;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.EditPersonalDataDto;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.service.dto.response.EditedPasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/{id}")
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
    @PatchMapping("/")
    public ResponseEntity<EditedEmailNameDto> editEmail(@PathVariable Long id, @RequestBody EditPersonalDataDto editPersonalDataDto) {
        EditedEmailNameDto editEmail = userService.editPersonData(id, editPersonalDataDto);
        return ResponseEntity.ok(editEmail);
    }

    // edit password of the user by id
    @PatchMapping("/password")
    public ResponseEntity<EditedPasswordDto> editPassword(@PathVariable Long id, @RequestBody EditPersonalDataDto editPersonalDataDto) {
        EditedPasswordDto editedPasswordDto = userService.editPassword(id, editPersonalDataDto.getNewPassword(), editPersonalDataDto.getRawOldPassword());
        return ResponseEntity.ok(editedPasswordDto);
    }

    // create new user from accountDto
    @PostMapping("/")
    public ResponseEntity<EditedEmailNameDto> createUser(@RequestBody AccountDto accountDto){
        EditedEmailNameDto newUser = userService.createUser(accountDto);
        return ResponseEntity.ok(newUser);
    }

    // delete user by email from accountDto
    @DeleteMapping("/")
    public ResponseEntity<AccountDto> deleteUser(@PathVariable Long id){
        AccountDto accountDto = userService.deleteUser(id, null);
        return ResponseEntity.ok(accountDto);
    }
}
