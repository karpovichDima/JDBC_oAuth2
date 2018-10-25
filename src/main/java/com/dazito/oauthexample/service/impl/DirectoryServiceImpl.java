package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.DirectoryRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.ResponseCode;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.*;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import com.dazito.oauthexample.service.dto.response.DirectoryDeletedDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Autowired
    private UserService userServices;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private ContentService contentService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private DirectoryRepository directoryRepository;


    // create new Directory by parent id and name
    @Override
    public DirectoryCreatedDto createDirectory(DirectoryDto directoryDto) throws AppException {
        AccountEntity currentUser = userServices.getCurrentUser();
        String name = directoryDto.getNewName();
        Long parent_id = directoryDto.getNewParentId();
        Organization organization = currentUser.getOrganization();
        UserRole role = currentUser.getRole();
        StorageElement foundParentElement;

        Directory directory = new Directory();
        directory.setName(name);
        directory.setSize(0L);
        directory.setOrganization(currentUser.getOrganization());
        if (parent_id == 0) {
            foundParentElement = contentService.findContentForAdmin(organization);
        } else {
            foundParentElement = storageService.findById(parent_id);
            SomeType type = foundParentElement.getType();
            if (type.equals(SomeType.FILE))
                throw new AppException("A different type of object was expected.", ResponseCode.TYPE_MISMATCH);
        }
//        directory.setParent(foundParentElement);
        if (role.equals(UserRole.USER) && !foundParentElement.getType().equals(SomeType.CONTENT)) {
            AccountEntity owner = foundParentElement.getOwner();
            userServices.isMatchesEmail(currentUser.getEmail(), owner.getEmail());
        }
        directory.setOwner(currentUser);
        storageRepository.saveAndFlush(directory);
        return responseDirectoryCreated(directory);
    }

    @Override
    public DirectoryCreatedDto responseDirectoryCreated(Directory directory) {
        String nameDir = directory.getName();
//        StorageElement parentDir = directory.getParent();
//        Long idDir = parentDir.getId();
//
        DirectoryCreatedDto directoryResponseDto = new DirectoryCreatedDto();
//        directoryResponseDto.setParentId(idDir);
        directoryResponseDto.setName(nameDir);
        return directoryResponseDto;
    }

    @Override
    public DirectoryCreatedDto updateDirectory(DirectoryDto directoryDto) throws AppException {
        AccountEntity currentUser = userServices.getCurrentUser();
        Long id = directoryDto.getId();
        Long parent = directoryDto.getNewParentId();
        String name = directoryDto.getNewName();
        Organization organization = currentUser.getOrganization();

        StorageElement foundDirectory = storageService.findById(id);
        AccountEntity owner = foundDirectory.getOwner();
        Organization organizationDirectory = foundDirectory.getOrganization();
        StorageElement parentDirectory = storageService.findById(parent);

        boolean isRight = utilService.isPermissionsAdminOrUserIsOwner(currentUser, owner, foundDirectory);
        if (!isRight) throw new AppException("You are not allowed to change", ResponseCode.CURRENT_USER_IS_NOT_ADMIN);
        utilService.isMatchesOrganization(organization.getOrganizationName(),
                                          organizationDirectory.getOrganizationName());
//        foundDirectory.setParent(parentDirectory);
        foundDirectory.setName(name);
        storageRepository.saveAndFlush(foundDirectory);
        return responseDirectoryCreated((Directory) foundDirectory);
    }

    @Transactional
    @Override
    public DirectoryDeletedDto delete(Long id) throws AppException {
        AccountEntity currentUser = userServices.getCurrentUser();
        StorageElement foundStorage = findById(id);
        AccountEntity owner = foundStorage.getOwner();
        SomeType type = foundStorage.getType();

        if (type == SomeType.FILE) throw new AppException("A different type of object was expected.", ResponseCode.TYPE_MISMATCH);

        StorageElementWithChildren downcastFoundStorage = (StorageElementWithChildren)foundStorage;

        boolean canChange = utilService.isPermissionsAdminOrUserIsOwner(currentUser, owner, downcastFoundStorage);
        if (!canChange) throw new AppException("You are not allowed to change", ResponseCode.CURRENT_USER_IS_NOT_ADMIN);
        String organizationNameCurrentUser = currentUser.getOrganization().getOrganizationName();
        String organizationNameFound = downcastFoundStorage.getOrganization().getOrganizationName();
        utilService.isMatchesOrganization(organizationNameCurrentUser,organizationNameFound);

        List<StorageElement> listChildToDelete = new ArrayList<>();
        List<StorageElement> listChildrenFoundStorage = downcastFoundStorage.getChildren();
        listChildToDelete.add(downcastFoundStorage);
        listChildToDelete.addAll(listChildrenFoundStorage);
        deleteChildFiles(listChildToDelete, listChildrenFoundStorage);
        storageRepository.delete(listChildToDelete);

        DirectoryDeletedDto directoryDeletedDto = new DirectoryDeletedDto();
        directoryDeletedDto.setId(id);
        directoryDeletedDto.setName(downcastFoundStorage.getName());
//        directoryDeletedDto.setParentId(foundStorage.getParent().getId());
        return directoryDeletedDto;
    }

    @Override
    public void deleteChildFiles(List<StorageElement> listChildToDelete, List<StorageElement> listChildrenFoundStorage) {
        List<StorageElement> childrenElement;
        for (StorageElement element : listChildrenFoundStorage) {
            if (element.getType() != SomeType.FILE) {
                StorageElementWithChildren downcastElement = (StorageElementWithChildren) element;
                childrenElement = downcastElement.getChildren();
                listChildToDelete.addAll(childrenElement);
                deleteChildFiles(listChildToDelete, childrenElement);
            }
        }
    }

    private Directory findById(Long id) {
        Optional<Directory> foundDirectory = directoryRepository.findById(id);
        return foundDirectory.orElse(null);
    }


}
