package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Directory;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.DirectoryService;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    private final UserService userServices;
    private final FileService fileService;
    private final StorageRepository storageRepository;

    @Autowired
    public DirectoryServiceImpl(StorageRepository storageRepository, FileService fileService, UserService userServices) {
        this.storageRepository = storageRepository;
        this.fileService = fileService;
        this.userServices = userServices;
    }


    // create new Directory by parent id and name
    @Override
    public DirectoryCreatedDto createDirectory(DirectoryDto directoryDto) {
        AccountEntity currentUser = userServices.getCurrentUser();
        String name = directoryDto.getNewName();
        Long parent_id = directoryDto.getNewParentId();

        UserRole role = currentUser.getRole();

        StorageElement foundParentElement;

        Directory directory = new Directory();
        directory.setName(name);
        directory.setSize(0L);
        directory.setOrganization(currentUser.getOrganization());

        if (parent_id == 0) {
            foundParentElement = fileService.findByNameInStorageRepo("CONTENT");
        } else {
            foundParentElement = fileService.findByIdInStorageRepo(parent_id);
            SomeType type = foundParentElement.getType();
            if (type.equals(SomeType.FILE)) return null;
        }

        directory.setParentId(foundParentElement);

        if (role.equals(UserRole.USER) && !foundParentElement.getType().equals(SomeType.CONTENT)) {
            AccountEntity owner = foundParentElement.getOwner();
            if (!owner.getEmail().equals(currentUser.getEmail()))return null;
        }

        directory.setOwner(currentUser);

        storageRepository.saveAndFlush(directory);

        return responseDirectoryCreated(directory);
    }


    @Override
    public DirectoryCreatedDto responseDirectoryCreated(Directory directory) {
        String nameDir = directory.getName();
        StorageElement parentDir = directory.getParentId();
        Long idDir = parentDir.getId();

        DirectoryCreatedDto directoryResponseDto = new DirectoryCreatedDto();

        directoryResponseDto.setParentId(idDir);

        return directoryResponseDto;
    }

    @Override
    public DirectoryCreatedDto updateDirectory(DirectoryDto directoryDto) {
        AccountEntity currentUser = userServices.getCurrentUser();
        Long id = directoryDto.getId();
        Long parent = directoryDto.getNewParentId();
        String name = directoryDto.getNewName();

        StorageElement foundDirectory = fileService.findByIdInStorageRepo(id);
        if(foundDirectory == null) return null;
        AccountEntity owner = foundDirectory.getOwner();
        StorageElement parentDirectory = fileService.findByIdInStorageRepo(parent);

        boolean canChange = fileService.checkPermissionsOnStorageChanges(currentUser, owner, foundDirectory);
        if (!canChange) return null;

        foundDirectory.setParentId(parentDirectory);
        foundDirectory.setName(name);

        storageRepository.saveAndFlush(foundDirectory);

        return responseDirectoryCreated((Directory) foundDirectory);
    }




}
