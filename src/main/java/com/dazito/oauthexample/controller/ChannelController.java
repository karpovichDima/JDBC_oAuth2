package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.dao.ChannelRepository;
import com.dazito.oauthexample.model.Channel;
import com.dazito.oauthexample.service.ChannelService;
import com.dazito.oauthexample.service.dto.request.StorageAddToChannelDto;
import com.dazito.oauthexample.service.dto.request.UserAddToChannelDto;
import com.dazito.oauthexample.service.dto.response.ChannelCreatedDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.service.dto.response.StorageAddedToChannelDto;
import com.dazito.oauthexample.service.dto.response.UserAddedToChannelDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/storage/access/{idChannel:.+}")
    public ResponseEntity<GeneralResponseDto<List<Long>>> getAllStorageElements(@PathVariable Long idChannel) throws AppException {
        List<Long> allStorageElements = channelService.getAllStorageElements(idChannel);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, allStorageElements));
    }

    @GetMapping("/{idChannel:.+}/{id:.+}")
    public ResponseEntity<Resource> download(@PathVariable Long idChannel, @PathVariable Long id) throws IOException, AppException {
        Resource download = channelService.download(idChannel, id);
        return ResponseEntity.ok(download);
    }

}
