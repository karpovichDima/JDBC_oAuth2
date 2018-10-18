package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.ChannelRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Channel;
import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.ResponseCode;
import com.dazito.oauthexample.service.ChannelService;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.StorageService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.StorageAddToChannelDto;
import com.dazito.oauthexample.service.dto.request.UserAddToChannelDto;
import com.dazito.oauthexample.service.dto.response.ChannelCreatedDto;
import com.dazito.oauthexample.service.dto.response.StorageAddedToChannelDto;
import com.dazito.oauthexample.service.dto.response.UserAddedToChannelDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    UserService userService;
    @Autowired
    StorageRepository storageRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    StorageService storageService;

    @Override
    public ChannelCreatedDto createChannel(String name) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        userService.adminRightsCheck(currentUser);
        Channel channel = new Channel();
        channel.setOwner(currentUser);
        channel.setChannelName(name);
        ArrayList<AccountEntity> listAccount = new ArrayList<>();
        channel.setAccountEntityList(listAccount);
        ArrayList<StorageElement> listFiles = new ArrayList<>();
        channel.setStorageElementList(listFiles);

        channelRepository.saveAndFlush(channel);

        ChannelCreatedDto channelCreatedDto = new ChannelCreatedDto();
        channelCreatedDto.setChannelName(name);
        return channelCreatedDto;
    }

    @Override
    @Transactional
    public UserAddedToChannelDto addUserToChannel(UserAddToChannelDto userAddToChannelDto) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        userService.adminRightsCheck(currentUser);
        Long idUser = userAddToChannelDto.getIdUser();
        AccountEntity foundUser = userService.findByIdAccountRepo(idUser);
        String organizationNameFoundUser = foundUser.getOrganization().getOrganizationName();
        userService.isMatchesOrganization(organizationNameFoundUser, currentUser);

        Long idChannel = userAddToChannelDto.getIdChannel();
        Channel foundChannel = findById(idChannel);
        List<AccountEntity> userListFromChannel = foundChannel.getAccountEntityList();
        userListFromChannel.add(foundUser);
        foundChannel.setAccountEntityList(userListFromChannel);
        channelRepository.saveAndFlush(foundChannel);

        UserAddedToChannelDto userAddedToChannelDto = new UserAddedToChannelDto();
        userAddedToChannelDto.setIdChannel(idChannel);
        userAddedToChannelDto.setIdUser(idUser);
        return userAddedToChannelDto;
    }

    @Override
    @Transactional
    public StorageAddedToChannelDto addStorageToChannel(StorageAddToChannelDto storageAddToChannelDto) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        userService.adminRightsCheck(currentUser);
        Long idStorage = storageAddToChannelDto.getIdStorage();
        StorageElement foundStorageElement = storageService.findById(idStorage);
        String organizationNameFoundStorage = foundStorageElement.getOrganization().getOrganizationName();
        userService.isMatchesOrganization(organizationNameFoundStorage, currentUser);

        Long idChannel = storageAddToChannelDto.getIdChannel();
        Channel foundChannel = findById(idChannel);
        List<StorageElement> storageElementList = foundChannel.getStorageElementList();
        storageElementList.add(foundStorageElement);
        foundChannel.setStorageElementList(storageElementList);
        channelRepository.saveAndFlush(foundChannel);

        StorageAddedToChannelDto storageAddedToChannelDto = new StorageAddedToChannelDto();
        storageAddedToChannelDto.setIdChannel(idChannel);
        storageAddedToChannelDto.setIdStorage(idStorage);
        return storageAddedToChannelDto;
    }

    public Channel findById(Long id) throws AppException {
        Optional<Channel> foundOptional = channelRepository.findById(id);
        if (!foundOptional.isPresent()) throw new AppException("No objects were found by your request.", ResponseCode.NO_SUCH_ELEMENT);
        return foundOptional.get();
    }
}
