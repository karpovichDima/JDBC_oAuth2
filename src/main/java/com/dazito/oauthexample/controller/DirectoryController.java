package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.DirectoryService;
import com.dazito.oauthexample.service.dto.response.DirectoryDeletedDto;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import com.dazito.oauthexample.utils.exception.CurrentUserIsNotAdminException;
import com.dazito.oauthexample.utils.exception.EmailIsNotMatchesException;
import com.dazito.oauthexample.utils.exception.OrganizationIsNotMuchException;
import com.dazito.oauthexample.utils.exception.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/dirs")
public class DirectoryController {

    private final DirectoryService directoryService;

    @Autowired
    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @PostMapping("/")
    public ResponseEntity<DirectoryCreatedDto> create(@RequestBody DirectoryDto directoryDto) throws EmailIsNotMatchesException, TypeMismatchException {
        DirectoryCreatedDto directory = directoryService.createDirectory(directoryDto);
        return ResponseEntity.ok(directory);
    }

    @PatchMapping("/update")
    public ResponseEntity<DirectoryCreatedDto> update(@RequestBody DirectoryDto directoryDto) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException {
        DirectoryCreatedDto directoryCreatedDto = directoryService.updateDirectory(directoryDto);
        return ResponseEntity.ok(directoryCreatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DirectoryDeletedDto> delete(@PathVariable Long id) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException {
        DirectoryDeletedDto deleted = directoryService.delete(id);
        return ResponseEntity.ok(deleted);
    }
}
