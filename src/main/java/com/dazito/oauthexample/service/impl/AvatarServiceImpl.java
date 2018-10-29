package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.AvatarRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.ResponseCode;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.*;
import com.dazito.oauthexample.service.dto.response.AvatarCreatedDto;
import com.dazito.oauthexample.service.dto.response.AvatarDeletedDto;
import com.dazito.oauthexample.utils.exception.AppException;
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvatarServiceImpl implements AvatarService {

    @Autowired
    UserService userService;
    @Autowired
    ContentService contentService;
    @Autowired
    FileService fileService;
    @Autowired
    StorageService storageService;
    @Autowired
    StorageRepository storageRepository;
    @Autowired
    AvatarRepository avatarRepository;
    @Autowired
    UtilService utilService;

    @Value("${root.path}")
    Path root;

    @Override
    @Transactional
    public AvatarCreatedDto createAvatar(Long ownerId, MultipartFile file) throws AppException, IOException {
        uploadAndSetAvatar(ownerId, file);
        return avatarCreateResponse(ownerId);
    }

    private AvatarCreatedDto avatarCreateResponse(Long ownerId) {
        AvatarCreatedDto avatarCreatedDto = new AvatarCreatedDto();
        avatarCreatedDto.setAvatarOwnerId(ownerId);
        return avatarCreatedDto;
    }

    private void uploadAndSetAvatar(Long ownerId, MultipartFile file) throws AppException, IOException {
        String originalFilename = file.getOriginalFilename();
        AccountEntity currentUser = userService.getCurrentUser();
        Content findContent = null;
        Content foundContent = checkAccessFindContent(currentUser, findContent);
        Path rootPath = this.root;

        StorageElement foundStorage = storageService.findById(ownerId);
        if (foundStorage == null)
            throw new AppException("Such owner owner_id does not exist", ResponseCode.NO_SUCH_ELEMENT);

        String extension = FilenameUtils.getExtension(originalFilename);
        String name = FilenameUtils.getBaseName(originalFilename);
        String uuidString = fileService.generateStringUuid();
        String pathNewFile = rootPath + File.separator + uuidString;
        file.transferTo(new File(pathNewFile));

        Long size = file.getSize();

        Avatar avatar = new Avatar();
        avatar.setExtension(extension);
        avatar.setName(name);
        avatar.setSize(size);
        avatar.setOrganization(currentUser.getOrganization());
        avatar.setUuid(uuidString);

        List<StorageElement> parents = new ArrayList<>();
        parents.add(foundContent);
        avatar.setParents(parents);

        avatarRepository.saveAndFlush(avatar);

        if (foundStorage == null) throw new AppException("Found storage equal null", ResponseCode.NO_SUCH_ELEMENT);

        Channel castedFoundStorage = (Channel) foundStorage;
        castedFoundStorage.setAvatar(avatar);
        Channel channel = castedFoundStorage;
        storageRepository.saveAndFlush(channel);
    }

    private Content checkAccessFindContent(AccountEntity currentUser, Content foundContent) throws AppException {
        Organization organizationCurrentUser = currentUser.getOrganization();
        UserRole role = currentUser.getRole();
        String rootReference = null;

        switch (role) {
            case USER:
                foundContent = contentService.findContentByUser(currentUser);
                rootReference = foundContent.getRoot();
                break;
            case ADMIN:
                foundContent = contentService.findContentForAdmin(organizationCurrentUser);
                if (foundContent == null) foundContent = contentService.createContent(currentUser);
                rootReference = foundContent.getRoot();
                break;
        }
        Path rootPath;
        rootPath = this.root;

        if (role == UserRole.USER) {
            if (!foundContent.getOwner().getId().equals(currentUser.getId())) {
                throw new AppException("You are trying to upload a file where you do not have access.", ResponseCode.DO_NOT_HAVE_ACCESS);
            }
            rootPath = Paths.get(rootReference);
            if (!Files.exists(rootPath))
                throw new AppException("The path does not exist or has an error.", ResponseCode.PATH_NOT_EXIST);
        } else {
            Organization organizationParent = foundContent.getOrganization();
            utilService.isMatchesOrganization(organizationCurrentUser.getOrganizationName(), organizationParent.getOrganizationName());
        }

        return foundContent;
    }

    @Override
    @Transactional
    public Resource getAvatar(Long idAvatarOwner) throws AppException, IOException {
        AccountEntity currentUser = userService.getCurrentUser();
        Content findContent = null;
        checkAccessFindContent(currentUser, findContent);

        StorageElement foundStorage = storageService.findById(idAvatarOwner);
        SomeType type = foundStorage.getType();
        Channel channel;
        Avatar avatar;
        String uuid = null;
        switch (type) {
            case CHANNEL:
                channel = (Channel) foundStorage;
                avatar = channel.getAvatar();
                uuid = avatar.getUuid();
                break;
        }
        Long idCurrent = currentUser.getId();
        StorageElement fileEntityFromDB = fileService.findByUUID(uuid);
        AccountEntity fileOwner = fileEntityFromDB.getOwner();
        Long ownerId = fileOwner.getId();
        if (!utilService.matchesOwner(idCurrent, ownerId)) {
            userService.adminRightsCheck(currentUser);
        }
        Path filePath = fileService.setFilePathDependingOnTheUserRole(currentUser, uuid);
        if (!Files.exists(filePath))
            throw new AppException("The path does not exist or has an error.", ResponseCode.PATH_NOT_EXIST);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
        return resource;
    }

    @Override
    @Transactional
    public AvatarDeletedDto deleteAvatar(Long idAvatarOwner) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        Content findContent = null;
        checkAccessFindContent(currentUser, findContent);

        StorageElement foundAvatarOwner = storageService.findById(idAvatarOwner);
        SomeType type = foundAvatarOwner.getType();
        Channel channel;
        Avatar avatar = null;
        switch (type) {
            case CONTENT:
                break;
            case DIRECTORY:
                break;
            case FILE:
                break;
            case CHANNEL:
                channel = (Channel) foundAvatarOwner;
                avatar = channel.getAvatar();
                channel.setAvatar(null);
                break;
        }
        storageRepository.delete(avatar);
        return avatarDeletedResponse(idAvatarOwner);
    }

    private AvatarDeletedDto avatarDeletedResponse(Long idAvatarOwner) {
        AvatarDeletedDto avatarDeletedDto = new AvatarDeletedDto();
        avatarDeletedDto.setAvatarOwnerId(idAvatarOwner);
        return avatarDeletedDto;
    }
}
