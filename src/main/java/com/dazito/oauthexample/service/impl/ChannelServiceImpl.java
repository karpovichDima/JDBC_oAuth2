package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.ChannelRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Channel;
import com.dazito.oauthexample.service.ChannelService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.response.ChannelCreatedDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    UserService userService;
    @Autowired
    ChannelRepository channelRepository;

    @Override
    public ChannelCreatedDto createChannel(String name) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        userService.adminRightsCheck(currentUser);
        Channel channel = new Channel();
        channel.setOwner(currentUser);
        channel.setChannelName(name);
        channelRepository.saveAndFlush(channel);

        ChannelCreatedDto channelCreatedDto = new ChannelCreatedDto();
        channelCreatedDto.setChannelName(name);
        return channelCreatedDto;
    }
}
