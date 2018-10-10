package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.DirectoryRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Directory;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.*;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public DirectoryCreatedDto createDirectory(DirectoryDto directoryDto) {
        AccountEntity currentUser = userServices.getCurrentUser();
        String name = directoryDto.getNewName();
        Long parent_id = directoryDto.getNewParentId();
        String organizationName = currentUser.getOrganization().getOrganizationName();

        UserRole role = currentUser.getRole();

        StorageElement foundParentElement;

        Directory directory = new Directory();
        directory.setName(name);
        directory.setSize(0L);
        directory.setOrganization(currentUser.getOrganization());

        if (parent_id == 0) {
            foundParentElement = contentService.findContentForAdmin(organizationName);
        } else {
            foundParentElement = storageService.findById(parent_id);
            SomeType type = foundParentElement.getType();
            if (type.equals(SomeType.FILE)) return null;
        }

        directory.setParentId(foundParentElement);

        if (role.equals(UserRole.USER) && !foundParentElement.getType().equals(SomeType.CONTENT)) {
            AccountEntity owner = foundParentElement.getOwner();
            if (!owner.getEmail().equals(currentUser.getEmail())) return null;
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
        directoryResponseDto.setName(nameDir);
        return directoryResponseDto;
    }

    @Override
    public DirectoryCreatedDto updateDirectory(DirectoryDto directoryDto) {
        AccountEntity currentUser = userServices.getCurrentUser();
        Long id = directoryDto.getId();
        Long parent = directoryDto.getNewParentId();
        String name = directoryDto.getNewName();
        Organization organization = currentUser.getOrganization();

        StorageElement foundDirectory = storageService.findById(id);
        if (foundDirectory == null) return null;
        AccountEntity owner = foundDirectory.getOwner();
        Organization organizationDirectory = foundDirectory.getOrganization();
        StorageElement parentDirectory = storageService.findById(parent);

        boolean isRight = utilService.isPermissionsAdminOrUserIsOwner(currentUser, owner, foundDirectory);
        if (!isRight) return null;
        boolean isMatch = utilService.matchesOrganizations(organization, organizationDirectory);
        if (!isMatch) return null;

        foundDirectory.setParentId(parentDirectory);
        foundDirectory.setName(name);

        storageRepository.saveAndFlush(foundDirectory);

        return responseDirectoryCreated((Directory) foundDirectory);
    }

    @Override
    public void delete(Long id) {
        AccountEntity currentUser = userServices.getCurrentUser();
        StorageElement foundStorage = findById(id);
        AccountEntity owner = foundStorage.getOwner();
        SomeType type = foundStorage.getType();

        if (type.equals(SomeType.FILE)) return;

        boolean canChange = utilService.isPermissionsAdminOrUserIsOwner(currentUser, owner, foundStorage);
        if (!canChange) return;
        canChange = utilService.checkPermissionsOnChangeByOrganization(currentUser, foundStorage);
        if (!canChange) return;

        List<StorageElement> childChild = new ArrayList<>();

        List<StorageElement> listChildren = storageRepository.findByParentId(foundStorage);
        childChild.add(foundStorage);
        deleteChildFiles(childChild, listChildren);
        storageRepository.delete(childChild);
    }

    private void deleteChildFiles(List<StorageElement> childChild, List<StorageElement> listChildren) {
        for (StorageElement element : listChildren) {
            childChild.add(element);
            List<StorageElement> listChildrenElement = storageRepository.findByParentId(element);
            List<StorageElement> byParentId = storageRepository.findByParentId(element);
            if (byParentId.size() != 0) deleteChildFiles(childChild, listChildren);
        }
    }

    private Directory findById(Long id) {
        Optional<Directory> foundDirectory = directoryRepository.findById(id);
        return foundDirectory.orElse(null);
    }


}
