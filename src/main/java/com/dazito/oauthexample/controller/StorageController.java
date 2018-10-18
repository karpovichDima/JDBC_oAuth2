package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.StorageService;
import com.dazito.oauthexample.service.dto.request.StorageUpdateDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import com.dazito.oauthexample.service.dto.response.StorageUpdatedDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GeneralResponseDto<StorageUpdatedDto>> update(@RequestBody StorageUpdateDto storageUpdateDto) throws IOException, AppException {
        StorageUpdatedDto storageUpdatedDto = storageService.editData(storageUpdateDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, storageUpdatedDto));
    }

    @GetMapping("/hierarchy/{id:.+}")
    public ResponseEntity<GeneralResponseDto<StorageDto>> createHierarchy(@PathVariable Long id) throws IOException, AppException {
        StorageDto hierarchy = storageService.createHierarchy(id);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, hierarchy));
    }
}
