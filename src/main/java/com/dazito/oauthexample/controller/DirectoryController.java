package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.DirectoryService;
import com.dazito.oauthexample.service.dto.response.DirectoryDeletedDto;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.utils.exception.AppException;
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
    public ResponseEntity<GeneralResponseDto<DirectoryCreatedDto>> create(@RequestBody DirectoryDto directoryDto) throws AppException {
        DirectoryCreatedDto directory = directoryService.createDirectory(directoryDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, directory));
    }

    @PatchMapping("/update")
    public ResponseEntity<GeneralResponseDto<DirectoryCreatedDto>> update(@RequestBody DirectoryDto directoryDto) throws AppException {
        DirectoryCreatedDto directoryCreatedDto = directoryService.updateDirectory(directoryDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, directoryCreatedDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponseDto<DirectoryDeletedDto>> delete(@PathVariable Long id) throws AppException {
        DirectoryDeletedDto deleted = directoryService.delete(id);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, deleted));
    }
}
