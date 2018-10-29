package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.AvatarService;
import com.dazito.oauthexample.service.dto.response.AvatarCreatedDto;
import com.dazito.oauthexample.service.dto.response.AvatarDeletedDto;
import com.dazito.oauthexample.service.dto.response.DeletedStorageDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "/avatar/")
public class AvatarController {

    @Autowired
    AvatarService avatarService;

    @PostMapping("/create/{idAvatarOwner:.+}")
    public ResponseEntity<GeneralResponseDto<AvatarCreatedDto>> createAvatar(@RequestParam MultipartFile file, @PathVariable Long idAvatarOwner) throws AppException, IOException {
        AvatarCreatedDto avatar = avatarService.createAvatar(idAvatarOwner, file);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, avatar));
    }

    @GetMapping("/{idAvatarOwner:.+}")
    public ResponseEntity<Resource> getAvatar(@PathVariable Long idAvatarOwner) throws AppException, IOException {
        Resource avatar = avatarService.getAvatar(idAvatarOwner);
        return ResponseEntity.ok(avatar);
    }

    @DeleteMapping("/{idAvatarOwner:.+}")
    public ResponseEntity<GeneralResponseDto<AvatarDeletedDto>> deleteAvatar(@PathVariable Long idAvatarOwner) throws AppException {
        AvatarDeletedDto avatarDeletedDto = avatarService.deleteAvatar(idAvatarOwner);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, avatarDeletedDto));
    }
}
