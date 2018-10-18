package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.ChannelService;
import com.dazito.oauthexample.service.dto.request.StorageAddToChannelDto;
import com.dazito.oauthexample.service.dto.request.UserAddToChannelDto;
import com.dazito.oauthexample.service.dto.response.*;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/channel")
public class ChannelController {

    @Autowired
    ChannelService channelService;

    @PostMapping("/{name:.+}")
    public ResponseEntity<GeneralResponseDto<ChannelCreatedDto>> createChannel(@PathVariable String name) throws AppException {
        ChannelCreatedDto channel = channelService.createChannel(name);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, channel));
    }

    @PostMapping("/add/user")
    public ResponseEntity<GeneralResponseDto<UserAddedToChannelDto>> addUser(@RequestBody UserAddToChannelDto userAddToChannelDto) throws AppException {
        UserAddedToChannelDto response = channelService.addUserToChannel(userAddToChannelDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, response));
    }

    @PostMapping("/add/storage")
    public ResponseEntity<GeneralResponseDto<StorageAddedToChannelDto>> addStorageElement(@RequestBody StorageAddToChannelDto storageAddToChannelDto) throws AppException {
        StorageAddedToChannelDto response = channelService.addStorageToChannel(storageAddToChannelDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, response));
    }

}
