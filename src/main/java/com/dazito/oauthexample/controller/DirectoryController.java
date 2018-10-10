package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.DirectoryService;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.StorageService;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<DirectoryCreatedDto> create(@RequestBody DirectoryDto directoryDto) {
        DirectoryCreatedDto directory = directoryService.createDirectory(directoryDto);
        return ResponseEntity.ok(directory);
    }

    @PatchMapping("/update")
    public ResponseEntity<DirectoryCreatedDto> update(@RequestBody DirectoryDto directoryDto) {
        DirectoryCreatedDto directoryCreatedDto = directoryService.updateDirectory(directoryDto);
        return ResponseEntity.ok(directoryCreatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id){
        directoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
