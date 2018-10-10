package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.DirectoryService;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.StorageService;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(value = HttpStatus.OK)
    public DirectoryCreatedDto create(@RequestBody DirectoryDto directoryDto) {
        return directoryService.createDirectory(directoryDto);
    }

    @PatchMapping("/update")
    @ResponseStatus(value = HttpStatus.OK)
    public DirectoryCreatedDto update(@RequestBody DirectoryDto directoryDto) {
        return directoryService.updateDirectory(directoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable Long id){
        directoryService.delete(id);
    }
}
