package com.dazito.oauthexample.service;

import com.dazito.oauthexample.service.dto.request.UserAddToChannelDto;
import com.dazito.oauthexample.service.dto.response.ChannelCreatedDto;
import com.dazito.oauthexample.service.dto.response.UserAddedToChannelDto;
import com.dazito.oauthexample.utils.exception.AppException;

public interface ChannelService {

    ChannelCreatedDto createChannel(String id) throws AppException;

    UserAddedToChannelDto addUserToChannel(UserAddToChannelDto userAddToChannelDto) throws AppException;
}
