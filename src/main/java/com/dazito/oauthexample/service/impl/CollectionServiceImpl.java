package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.StorageElementWithChildren;
import com.dazito.oauthexample.service.*;
import com.dazito.oauthexample.service.dto.response.DeletedStorageDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    StorageService storageService;
    @Autowired
    StorageRepository storageRepository;
    @Autowired
    FileService fileService;
    @Autowired
    UserService userService;
    @Autowired
    UtilService utilService;

    @Override
    @Transactional
    public DeletedStorageDto deleteFileFromCollection(Long idCollection, Long idFile) throws AppException {
        FileEntity foundFile = fileService.findById(idFile);
        StorageElementWithChildren foundCollection = (StorageElementWithChildren)storageService.findById(idCollection);
        AccountEntity currentUser = userService.getCurrentUser();
        Long ownerId = foundCollection.getOwner().getId();
        if (ownerId != currentUser.getId()){
            userService.adminRightsCheck(currentUser);
            String organizationNameUser = currentUser.getOrganization().getOrganizationName();
            utilService.isMatchesOrganization(organizationNameUser, foundCollection.getOrganization().getOrganizationName());
        }
        List<StorageElement> children = foundCollection.getChildren();
        children.remove(foundFile);
        storageRepository.saveAndFlush(foundCollection);

        DeletedStorageDto deletedStorageDto = new DeletedStorageDto();
        deletedStorageDto.setNameDeletedStorage(foundFile.getName());
        return deletedStorageDto;
    }

    @Override
    @Transactional
    public DeletedStorageDto deleteCollection(Long idCollection) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        StorageElementWithChildren foundCollection = (StorageElementWithChildren)storageService.findById(idCollection);
        Long ownerId = foundCollection.getOwner().getId();
        if (ownerId != currentUser.getId()){
            userService.adminRightsCheck(currentUser);
            String organizationNameUser = currentUser.getOrganization().getOrganizationName();
            utilService.isMatchesOrganization(organizationNameUser, foundCollection.getOrganization().getOrganizationName());
        }
        String nameCollection = foundCollection.getName();
        List<StorageElement> children = foundCollection.getChildren();
        for (StorageElement child : children) {
            List<StorageElement> parents = child.getParents();
            parents.remove(foundCollection);
        }
        storageRepository.delete(foundCollection);

        DeletedStorageDto deletedStorageDto = new DeletedStorageDto();
        deletedStorageDto.setNameDeletedStorage(nameCollection);
        return deletedStorageDto;
    }
}
