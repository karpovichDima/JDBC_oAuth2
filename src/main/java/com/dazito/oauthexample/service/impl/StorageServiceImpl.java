package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Organization;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.service.StorageService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.UtilService;
import com.dazito.oauthexample.service.dto.request.StorageUpdateDto;
import com.dazito.oauthexample.service.dto.response.DirectoryStorageDto;
import com.dazito.oauthexample.service.dto.response.FileStorageDto;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import com.dazito.oauthexample.service.dto.response.StorageUpdatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StorageServiceImpl implements StorageService {

    @Resource(name = "conversionService")
    ConversionService conversionService;

    @Autowired
    private UserService userService;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private UtilService utilService;

    @Override
    public StorageUpdatedDto editData(StorageUpdateDto storageUpdateDto) {
        AccountEntity currentUser = userService.getCurrentUser();

        Long id = storageUpdateDto.getId();
        String newName = storageUpdateDto.getNewName();
        Long newParent = storageUpdateDto.getNewParentId();

        StorageElement foundStorageElement = findById(id);
        if (foundStorageElement == null) return null;
        AccountEntity owner = foundStorageElement.getOwner();
        StorageElement parent = findById(newParent);

        // check Permission on change
        boolean canChange = utilService.isPermissionsAdminOrUserIsOwner(currentUser, owner, foundStorageElement);
        if (!canChange) return null;
        Organization organizationUser = currentUser.getOrganization();
        Organization organizationStorage = foundStorageElement.getOrganization();
        canChange = utilService.matchesOrganizations(organizationUser, organizationStorage);
        if (!canChange) return null;

        foundStorageElement.setName(newName);
        foundStorageElement.setParentId(parent);

        storageRepository.saveAndFlush(foundStorageElement);

        return conversionService.convert(storageUpdateDto, StorageUpdatedDto.class);
    }

    @Override
    public StorageElement findById(Long id) {
        Optional<StorageElement> storageOptional = storageRepository.findById(id);
        return storageOptional.orElse(null);
    }

    @Override
    public StorageDto createHierarchy(Long id) {
        return buildStorageDto(id, null, 0);
    }

    @Override
    public StorageDto buildStorageDto(Long id, StorageDto storageDtoParent, long sizeFileParent) {
        StorageElement storageElement = findById(id);

        Long idElement = storageElement.getId();
        String nameElement = storageElement.getName();
        SomeType typeElement = storageElement.getType();

        StorageDto storageDto;

        if (typeElement.equals(SomeType.FILE)) {
            storageDto = new FileStorageDto();
        } else {
            storageDto = new DirectoryStorageDto();
        }

        storageDto.setId(idElement);
        storageDto.setName(nameElement);
        storageDto.setType(typeElement);
        storageDto.setParent(storageDtoParent);
        storageDto.setSize(sizeFileParent);

        if (typeElement.equals(SomeType.FILE)) return storageDto;


        List<StorageElement> elementChildren = getChildListElement(storageElement);

        List<StorageDto> listChildrenDirectories = new ArrayList<>();
        List<StorageDto> listChildrenFiles = new ArrayList<>();


        long sizeElementFile = 0;
        long parentSize;
        long currentStorageDtoSize;
        SomeType type = null;

        for (StorageElement element : elementChildren) {
            type = element.getType();
            long elementId = element.getId();
            if (type.equals(SomeType.DIRECTORY))
                listChildrenDirectories.add(buildStorageDto(elementId, storageDto, sizeElementFile));
            if (type.equals(SomeType.FILE)) {
                sizeElementFile = element.getSize();
                parentSize = storageDtoParent.getSize() + sizeElementFile;
                storageDtoParent.setSize(parentSize);
                currentStorageDtoSize = storageDto.getSize();
                currentStorageDtoSize = currentStorageDtoSize + sizeElementFile;
                storageDto.setSize(currentStorageDtoSize);

                listChildrenFiles.add(buildStorageDto(elementId, storageDto, sizeElementFile));
            }
        }
        if (storageDtoParent != null && !type.equals(SomeType.FILE)) {
            currentStorageDtoSize = storageDto.getSize();
            parentSize = storageDtoParent.getSize();
            parentSize = parentSize + currentStorageDtoSize;
            storageDtoParent.setSize(parentSize);
        }

        DirectoryStorageDto directoryStorageDtoDirectory = (DirectoryStorageDto) storageDto;
        directoryStorageDtoDirectory.setChildrenDirectories(listChildrenDirectories);
        directoryStorageDtoDirectory.setChildrenFiles(listChildrenFiles);

        return storageDto;
    }

    @Override
    public void setSizeForParents(Long size, StorageDto storageDtoParent) {

        if (storageDtoParent == null) return;
        Long sizeParent = storageDtoParent.getSize();
        sizeParent = sizeParent + size;
        storageDtoParent.setSize(sizeParent);
        if (storageDtoParent.getType().equals(SomeType.CONTENT)) return;
        StorageDto preParent = storageDtoParent.getParent();
        if (storageDtoParent.getParent() != null) setSizeForParents(size, preParent);
    }

    @Override
    public List<StorageElement> getChildListElement(StorageElement storageElement) {
        return storageRepository.findByParentId(storageElement);
    }
}
