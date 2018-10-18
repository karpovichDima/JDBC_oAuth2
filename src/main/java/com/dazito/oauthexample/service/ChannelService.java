package com.dazito.oauthexample.service;

import com.dazito.oauthexample.service.dto.response.ChannelCreatedDto;
import com.dazito.oauthexample.utils.exception.AppException;

public interface ChannelService {

    ChannelCreatedDto createChannel(String id) throws AppException;
}
