package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.Channel;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.request.StorageAddToSomeStructureDto;
import com.dazito.oauthexample.service.dto.request.UpdateStorageOnChannel;
import com.dazito.oauthexample.service.dto.request.UserAddToChannelDto;
import com.dazito.oauthexample.service.dto.response.*;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

public interface ChannelService {

    UserAddedToChannelDto addUserToChannel(UserAddToChannelDto userAddToChannelDto) throws AppException;

    List<Long> getAllStorageElementsChannel(Long idChannel) throws AppException;

    Resource download(Long idChannel, Long id) throws AppException, IOException;

    DirectoryCreatedDto createDirectory(DirectoryDto directoryDto) throws AppException;

    DirectoryCreatedDto updateStorage(UpdateStorageOnChannel updateStorageOnChannel) throws AppException;

    DeletedStorageDto deleteChannel(Long idChannel) throws AppException;

    boolean isPartChannel(StorageElement child, StorageElement foundChannel);

    boolean checkStorageOnChannel(Channel foundChannel, StorageElement foundFile) throws AppException;

    @Transactional
    DeletedStorageDto deleteStorageFromChannel(Long idChannel, Long idStorage) throws AppException;
}
