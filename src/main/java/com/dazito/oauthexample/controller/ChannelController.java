package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.ChannelService;
import com.dazito.oauthexample.service.dto.response.ChannelCreatedDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
}
