package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.ChannelRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.ResponseCode;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.*;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.request.StorageAddToSomeStructureDto;
import com.dazito.oauthexample.service.dto.request.UpdateStorageOnChannel;
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
    ContentService contentService;
    @Autowired
    StorageService storageService;
    @Autowired
    UtilService utilService;
    @Autowired
    FileService fileService;

    @Value("${root.path}")
    Path root;

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
        Channel foundChannel = (Channel)findById(idChannel);
        List<AccountEntity> userListFromChannel = foundChannel.getListOwners();
        userListFromChannel.add(foundUser);
        foundChannel.setListOwners(userListFromChannel);

        channelRepository.saveAndFlush(foundChannel);

        UserAddedToChannelDto userAddedToChannelDto = new UserAddedToChannelDto();
        userAddedToChannelDto.setIdChannel(idChannel);
        userAddedToChannelDto.setIdUser(idUser);
        return userAddedToChannelDto;
    }

    @Override
    @Transactional
    public List<Long> getAllStorageElementsChannel(Long idChannel) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        Channel foundChannel = (Channel)findById(idChannel);

        boolean isHaveAccess = checkRightsCheck(currentUser, foundChannel);
        if (!isHaveAccess)
            throw new AppException("You do not have access to this channel", ResponseCode.DO_NOT_HAVE_ACCESS);

        List<StorageElement> storageElementList = foundChannel.getChildren();
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
        Channel foundChannel = (Channel) findById(idChannel);

        boolean isHaveAccess = checkRightsCheck(currentUser, foundChannel);
        if (!isHaveAccess)
            throw new AppException("You do not have access to this channel", ResponseCode.DO_NOT_HAVE_ACCESS);

        FileEntity foundFile = fileService.findById(id);
        AccountEntity ownerFile = foundFile.getOwner();
        UserRole roleOwnerFile = ownerFile.getRole();

        String uuid = foundFile.getUuid();
        Path filePath = null;

        switch (roleOwnerFile) {
            case USER:
                filePath = Paths.get(contentService.findContentByUser(ownerFile).getRoot(), uuid);
                break;
            case ADMIN:
                filePath = Paths.get(root.toString(), uuid);
                break;
        }
        boolean onChannel = checkStorageOnChannel(foundChannel, foundFile);
        if (!onChannel)
            throw new AppException("Storage element is not exist on channel.", ResponseCode.NO_SUCH_ELEMENT);

        if (!Files.exists(filePath))
            throw new AppException("The path does not exist or has an error.", ResponseCode.PATH_NOT_EXIST);

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
        return resource;
    }

    @Override
    @Transactional
    public DirectoryCreatedDto updateStorage(UpdateStorageOnChannel updateStorageOnChannel) throws AppException {
        Long idChannel = updateStorageOnChannel.getIdChannel();
        Long idEditStorage = updateStorageOnChannel.getIdEditStorage();
        Long idNewParent = updateStorageOnChannel.getIdNewParent();
        Long idCurrentParent = updateStorageOnChannel.getIdCurrentParent();
        if (idCurrentParent == null) idCurrentParent = idChannel;

        AccountEntity currentUser = userService.getCurrentUser();
        Channel foundChannel = (Channel)findById(idChannel);
        checkRightsCheck(currentUser, foundChannel);
        StorageElement foundStorage = storageService.findById(idEditStorage);
        checkStorageOnChannel(foundChannel, foundStorage);

        StorageElementWithChildren currentParent = (StorageElementWithChildren) findById(idCurrentParent);
        StorageElementWithChildren newParent = (StorageElementWithChildren) storageService.findById(idNewParent);
        List<StorageElement> childrenCurrentParent = currentParent.getChildren();
        childrenCurrentParent.remove(foundStorage);
        List<StorageElement> childrenNewParent = newParent.getChildren();
        childrenNewParent.add(foundStorage);

        channelRepository.saveAndFlush(foundChannel);

        DirectoryCreatedDto directoryCreatedDto = new DirectoryCreatedDto();
        directoryCreatedDto.setName(foundStorage.getName());
        directoryCreatedDto.setParentId(idNewParent);
        return directoryCreatedDto;
    }

    @Override
    public DeletedStorageDto deleteChannel(Long idChannel) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        Channel channel = (Channel)findById(idChannel);
        String name = channel.getName();
        String organizationNameChannel = channel.getOrganization().getOrganizationName();
        userService.isMatchesOrganization(organizationNameChannel, currentUser);
        channelRepository.delete(idChannel);

        DeletedStorageDto deletedStorageDto = new DeletedStorageDto();
        deletedStorageDto.setNameDeletedStorage(name);
        return deletedStorageDto;
    }

    @Override
    @Transactional
    public DirectoryCreatedDto createDirectory(DirectoryDto directoryDto) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        Long newParentId = directoryDto.getNewParentId();
        StorageElement foundParent = findById(newParentId);
        String organizationNameFoundStorage = foundParent.getOrganization().getOrganizationName();
        userService.isMatchesOrganization(organizationNameFoundStorage, currentUser);

        //TO ADD CHECK RIGHT

        Directory directory = new Directory();
        directory.setName(directoryDto.getNewName());
        directory.setOrganization(currentUser.getOrganization());
        directory.setOwner(currentUser);

        storageRepository.saveAndFlush(directory);

        Channel channelByStorage = findChannelByStorage(foundParent);

        StorageElementWithChildren castFoundParent = (StorageElementWithChildren) foundParent;

        List<StorageElement> children = castFoundParent.getChildren();
        if (children == null) children = new ArrayList<>();
        children.add(directory);

        ((StorageElementWithChildren) foundParent).setChildren(children);
        channelRepository.saveAndFlush(channelByStorage);

        DirectoryCreatedDto directoryCreatedDto = new DirectoryCreatedDto();
        directoryCreatedDto.setName(directoryDto.getNewName());
        directoryCreatedDto.setParentId(foundParent.getId());

        StorageElement foundDir = storageRepository.findByName(directoryDto.getNewName()).get();
        Long id = foundDir.getId();
        directoryCreatedDto.setId(id);
        return directoryCreatedDto;
    }



    @Override
    public boolean isPartChannel(StorageElement child, StorageElement foundChannel) {
        List<StorageElement> parents = child.getParents();
        for (StorageElement parent : parents) {
            if (parent.getId().equals(foundChannel.getId())){
                return true;
            }
            if (parent.getType() == SomeType.DIRECTORY) return isPartChannel(parent, foundChannel);
        }
        return false;
    }

    private Channel findChannelByStorage(StorageElement foundStorage) throws AppException {
        SomeType type = foundStorage.getType();
        switch (type) {
            case CONTENT:
                throw new AppException("The type cannot be a Content.", ResponseCode.TYPE_MISMATCH);
            case FILE:
                throw new AppException("The type cannot be a File.", ResponseCode.TYPE_MISMATCH);
            case CHANNEL:
                return (Channel) foundStorage;
            case DIRECTORY:
                return recursForFindChannelParent(foundStorage);
        }
        throw new AppException("Channel is not found.", ResponseCode.NO_SUCH_ELEMENT);
    }

    private Channel recursForFindChannelParent(StorageElement foundStorage) throws AppException {
        List<StorageElement> parents = foundStorage.getParents();
        for (StorageElement element : parents) {
            SomeType type = element.getType();
            if (type == SomeType.CHANNEL) return (Channel) element;
            if (type == SomeType.FILE) continue;
            recursForFindChannelParent(element);
        }
        return null;
    }

    @Override
    public boolean checkStorageOnChannel(Channel foundChannel, StorageElement foundFile) throws AppException {
        List<StorageElement> parents = foundFile.getParents();
        for (StorageElement element : parents) {
            if (element.getType() == SomeType.CHANNEL && element.getId() == foundChannel.getId()) return true;
            if (recursForFindChannelParent(element, foundChannel)) return true;
        }
        if (parents.isEmpty()) {
            if (foundFile.getType() == SomeType.CHANNEL && foundFile.getId() == foundChannel.getId()) return true;
        }
        return false;
    }

    private boolean recursForFindChannelParent(StorageElement transferElement, Channel foundChannel) throws AppException {
        List<StorageElement> parents = transferElement.getParents();
        for (StorageElement element : parents) {
            if (element.getType() == SomeType.CHANNEL && element.getId() == foundChannel.getId()) {
                return true;
            } else {
                recursForFindChannelParent(element, foundChannel);
            }
        }
        return false;
    }

    private boolean checkRightsCheck(AccountEntity currentUser, Channel foundChannel) throws AppException {
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

    public StorageElement findById(Long id) throws AppException {
        Optional<StorageElement> foundOptional = storageRepository.findById(id);
        if (!foundOptional.isPresent())
            throw new AppException("No objects were found by your request.", ResponseCode.NO_SUCH_ELEMENT);
        return foundOptional.get();
    }

}
