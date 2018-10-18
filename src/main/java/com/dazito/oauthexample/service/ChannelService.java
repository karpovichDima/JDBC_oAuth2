package com.dazito.oauthexample.service;

import com.dazito.oauthexample.service.dto.request.StorageAddToChannelDto;
import com.dazito.oauthexample.service.dto.request.UserAddToChannelDto;
import com.dazito.oauthexample.service.dto.response.ChannelCreatedDto;
import com.dazito.oauthexample.service.dto.response.StorageAddedToChannelDto;
import com.dazito.oauthexample.service.dto.response.UserAddedToChannelDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface ChannelService {

    ChannelCreatedDto createChannel(String id) throws AppException;

    UserAddedToChannelDto addUserToChannel(UserAddToChannelDto userAddToChannelDto) throws AppException;

    StorageAddedToChannelDto addStorageToChannel(StorageAddToChannelDto storageAddToChannelDto) throws AppException;

    List<Long> getAllStorageElements(Long idChannel) throws AppException;

    Resource download(Long idChannel, Long id) throws AppException, IOException;
}
