package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.ChannelRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Channel;
import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.ResponseCode;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.*;
import com.dazito.oauthexample.service.dto.request.StorageAddToChannelDto;
import com.dazito.oauthexample.service.dto.request.UserAddToChannelDto;
import com.dazito.oauthexample.service.dto.response.*;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    DirectoryService directoryService;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    StorageService storageService;
    @Autowired
    UtilService utilService;
    @Autowired
    FileService fileService;

    @Value("${root.path}")
    Path root;

    @Override
    public ChannelCreatedDto createChannel(String name) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        userService.adminRightsCheck(currentUser);
        Channel channel = new Channel();
        channel.setOwner(currentUser);
        channel.setName(name);
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

    @Override
    @Transactional
    public List<Long> getAllStorageElements(Long idChannel) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        Channel foundChannel = findById(idChannel);

        boolean isHaveAccess = changeRightsCheck(currentUser, foundChannel);
        if (!isHaveAccess) throw new AppException("You do not have access to this channel",ResponseCode.DO_NOT_HAVE_ACCESS);

        List<StorageElement> storageElementList = foundChannel.getStorageElementList();
        List<Long> storageElementListIds = new ArrayList<>();

        for (StorageElement element : storageElementList) {
            storageElementListIds.add(element.getId());
        }
        return storageElementListIds;
    }

    @Override
    @Transactional
    public Resource download(Long idChannel, Long id) throws AppException, IOException {
        AccountEntity currentUser = userService.getCurrentUser();
        Channel foundChannel = findById(idChannel);

        boolean isHaveAccess = changeRightsCheck(currentUser, foundChannel);
        if (!isHaveAccess) throw new AppException("You do not have access to this channel",ResponseCode.DO_NOT_HAVE_ACCESS);

        FileEntity foundFile = fileService.findById(id);
        AccountEntity ownerFile = foundFile.getOwner();
        UserRole roleOwnerFile = ownerFile.getRole();

        String uuid = foundFile.getUuid();
        Path filePath = null;

        switch (roleOwnerFile) {
            case USER:
                filePath = Paths.get(ownerFile.getContent().getRoot(), uuid);
                break;
            case ADMIN:
                filePath = Paths.get(root.toString(), uuid);
                break;
        }
        boolean fileOnChannel = checkStorageOnChannel(foundChannel, foundFile);
        if (!fileOnChannel) throw new AppException("The requested object is not on the selected channel.", ResponseCode.NO_FILE_ON_CHANNEL);
        if (!Files.exists(filePath)) throw new AppException("The path does not exist or has an error.", ResponseCode.PATH_NOT_EXIST);

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
        return resource;
    }

    @Override
    @Transactional
    public DeletedStorageDto deleteStorageFromChannel(Long idChannel, Long idStorage) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        userService.adminRightsCheck(currentUser);
        StorageElement foundStorageElement = storageService.findById(idStorage);
        SomeType typeFoundStorage = foundStorageElement.getType();
        String organizationNameFoundStorage = foundStorageElement.getOrganization().getOrganizationName();
        userService.isMatchesOrganization(organizationNameFoundStorage, currentUser);

        Channel foundChannel = findById(idChannel);
        boolean isIncludedInChannel = checkStorageOnChannel(foundChannel, foundStorageElement);
        if (!isIncludedInChannel)
            throw new AppException("StorageElement is not included in select channel.", ResponseCode.NO_SUCH_ELEMENT);

        List<StorageElement> storageElementListChannelFirstLevel = foundChannel.getStorageElementList();

        if (typeFoundStorage == SomeType.FILE) {
            String foundStorageElementName = foundStorageElement.getName();
            storageElementListChannelFirstLevel.remove(foundStorageElement);
            foundChannel.setStorageElementList(storageElementListChannelFirstLevel);
            channelRepository.saveAndFlush(foundChannel);

            DeletedStorageDto deletedStorageDto = new DeletedStorageDto();
            deletedStorageDto.setIdDeletedStorage(idStorage);
            deletedStorageDto.setNameDeletedStorage(foundStorageElementName);
            return deletedStorageDto;
        }

        DeletedStorageDto deletedStorageDto = new DeletedStorageDto();
        deletedStorageDto.setIdDeletedStorage(idStorage);
        return deletedStorageDto;
    }
//        List<StorageElement> listChildToDelete = new ArrayList<>();
//        List<StorageElement> listChildrenFoundStorage = foundStorageElement.getChildren();
//        listChildToDelete.add(foundStorageElement);
//        for (StorageElement element : listChildrenFoundStorage) {
//            boolean isPartChannel = checkStorageOnChannel(foundChannel, element);
//            if (isPartChannel) listChildToDelete.add(element);
//        }
//        getChildFiles(listChildToDelete, listChildrenFoundStorage);
//
//        List<StorageElement> listAllChildrenFoundChannel = new ArrayList<>();
//        getChildFiles(listAllChildrenFoundChannel, storageElementListChannelFirstLevel);
//
//
//
//        storageRepository.delete(listChildToDelete);

//        DirectoryDeletedDto directoryDeletedDto = new DirectoryDeletedDto();
//        directoryDeletedDto.setId(id);
//        directoryDeletedDto.setName(foundStorageElement.getName());
//        directoryDeletedDto.setParentId(foundStorageElement.getParent().getId());
//        return directoryDeletedDto;
//    }

    public void getChildFiles(List<StorageElement> listChildToDelete, List<StorageElement> listChildrenFoundStorage) {
        List<StorageElement> childrenElement;
        for (StorageElement element : listChildrenFoundStorage) {
            childrenElement = element.getChildren();
            if (childrenElement.size() != 0) {
                listChildToDelete.addAll(childrenElement);
                getChildFiles(listChildToDelete, childrenElement);
            }
        }
    }

    private boolean checkStorageOnChannel(Channel foundChannel, StorageElement foundStorage) {
        Long idFile = foundStorage.getId();
        List<StorageElement> foundStorageElementList = foundChannel.getStorageElementList();
        for (StorageElement element : foundStorageElementList) {
            if (element.getId().equals(idFile)) return true;
        }
    return false;
    }

    private boolean changeRightsCheck(AccountEntity currentUser, Channel foundChannel) throws AppException {
        UserRole role = currentUser.getRole();
        switch (role) {
            case USER:
                List<Channel> channelListUser = currentUser.getChannelList();
                Long idFoundChannel = foundChannel.getId();
                for (Channel channel : channelListUser) {
                    if (channel.getId().equals(idFoundChannel)) return true;
                }
                return false;
            case ADMIN:
                String organizationNameCurrentUser = currentUser.getOrganization().getOrganizationName();
                String organizationNameOwnerChannel = foundChannel.getOwner().getOrganization().getOrganizationName();
                utilService.isMatchesOrganization(organizationNameCurrentUser, organizationNameOwnerChannel);
                return true;
        }
    return true;
    }

    public Channel findById(Long id) throws AppException {
        Optional<Channel> foundOptional = channelRepository.findById(id);
        if (!foundOptional.isPresent()) throw new AppException("No objects were found by your request.", ResponseCode.NO_SUCH_ELEMENT);
        return foundOptional.get();
    }
}
