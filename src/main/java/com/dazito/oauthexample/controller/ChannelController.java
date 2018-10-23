package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.dao.ChannelRepository;
import com.dazito.oauthexample.service.ChannelService;
import com.dazito.oauthexample.service.dto.request.StorageAddToChannelDto;
import com.dazito.oauthexample.service.dto.request.UserAddToChannelDto;
import com.dazito.oauthexample.service.dto.response.*;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/channel")
public class ChannelController {

    @Autowired
    ChannelService channelService;

    @Autowired
    ChannelRepository channelRepository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/{name:.+}")
    public ResponseEntity<GeneralResponseDto<ChannelCreatedDto>> createChannel(@PathVariable String name) throws AppException {
        ChannelCreatedDto channel = channelService.createChannel(name);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, channel));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/add/user")
    public ResponseEntity<GeneralResponseDto<UserAddedToChannelDto>> addUser(@RequestBody UserAddToChannelDto userAddToChannelDto) throws AppException {
        UserAddedToChannelDto response = channelService.addUserToChannel(userAddToChannelDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, response));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/add/storage")
    public ResponseEntity<GeneralResponseDto<StorageAddedToChannelDto>> addStorageElement(@RequestBody StorageAddToChannelDto storageAddToChannelDto) throws AppException {
        StorageAddedToChannelDto response = channelService.addStorageToChannel(storageAddToChannelDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, response));
    }

    @GetMapping("/{idChannel:.+}/{id:.+}")
    public ResponseEntity<Resource> download(@PathVariable Long idChannel, @PathVariable Long id) throws IOException, AppException {
        Resource download = channelService.download(idChannel, id);
        return ResponseEntity.ok(download);
    }




    @GetMapping("/storage/access/{idChannel:.+}")
    public ResponseEntity<GeneralResponseDto<List<Long>>> getAllStorageElements(@PathVariable Long idChannel) throws AppException {
        List<Long> allStorageElements = channelService.getAllStorageElements(idChannel);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, allStorageElements));
    }

    @DeleteMapping("/{idChannel}/{idStorage}")
    public ResponseEntity<GeneralResponseDto<DeletedStorageDto>> deleteUser(@PathVariable Long idChannel, @PathVariable Long idStorage) throws AppException {
        DeletedStorageDto deletedStorageDto = channelService.deleteStorageFromChannel(idChannel, idStorage);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, deletedStorageDto));
    }


}
