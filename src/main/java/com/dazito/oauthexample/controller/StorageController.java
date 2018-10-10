package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.StorageService;
import com.dazito.oauthexample.service.dto.request.StorageUpdateDto;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import com.dazito.oauthexample.service.dto.response.StorageUpdatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "/storage")
public class StorageController {

    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PatchMapping("/")
    @ResponseStatus(value = HttpStatus.OK)
    public StorageUpdatedDto update(@RequestBody StorageUpdateDto storageUpdateDto) throws IOException {
        return storageService.editData(storageUpdateDto);
    }

    @GetMapping("/chierarchy/{id:.+}")
    @ResponseStatus(value = HttpStatus.OK)
    public StorageDto createHierarchy(@PathVariable Long id) throws IOException {
        return storageService.createHierarchy(id);
    }
}
