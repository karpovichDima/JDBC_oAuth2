package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.ResponseCode;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.service.StorageService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.UtilService;
import com.dazito.oauthexample.service.dto.request.StorageUpdateDto;
import com.dazito.oauthexample.service.dto.response.DirectoryStorageDto;
import com.dazito.oauthexample.service.dto.response.FileStorageDto;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import com.dazito.oauthexample.service.dto.response.StorageUpdatedDto;
import com.dazito.oauthexample.utils.exception.AppException;
import lombok.NonNull;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    public StorageUpdatedDto editData(StorageUpdateDto storageUpdateDto) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();

        Long id = storageUpdateDto.getId();
        String newName = storageUpdateDto.getNewName();
        Long newParent = storageUpdateDto.getNewParentId();

        StorageElement foundStorageElement = findById(id);
        AccountEntity owner = foundStorageElement.getOwner();
        StorageElement parent = findById(newParent);

        // check Permission on change
        boolean canChange = utilService.isPermissionsAdminOrUserIsOwner(currentUser, owner, foundStorageElement);
        if (!canChange) throw new AppException("You are not allowed to change", ResponseCode.CURRENT_USER_IS_NOT_ADMIN);
        String organizationNameCurrentUser = currentUser.getOrganization().getOrganizationName();
        String organizationNameFoundStorage = foundStorageElement.getOrganization().getOrganizationName();
        utilService.isMatchesOrganization(organizationNameCurrentUser, organizationNameFoundStorage);
        foundStorageElement.setName(newName);
        foundStorageElement.setParent(parent);

        storageRepository.saveAndFlush(foundStorageElement);
        return conversionService.convert(storageUpdateDto, StorageUpdatedDto.class);
    }

    @Override
    public StorageDto createHierarchy(Long id) throws AppException {
        return buildStorageDto(id, null, 0);
    }

    @Override
    public StorageDto buildStorageDto(Long id, StorageDto storageDtoParent, long sizeFileParent) throws AppException {
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

        if (typeElement == SomeType.FILE) return storageDto;

        List<StorageElement> elementChildren = getChildListElement(storageElement);

        List<StorageDto> listChildrenDirectories = new ArrayList<>();
        List<StorageDto> listChildrenFiles = new ArrayList<>();

        long sizeElementFile = 0;
        SomeType type = null;
        for (StorageElement element : elementChildren) {
            type = element.getType();
            long elementId = element.getId();
            switch (type) {
                case DIRECTORY:
                    listChildrenDirectories.add(buildStorageDto(elementId, storageDto, sizeElementFile));
                    break;
                case FILE:
                    sizeElementFile = element.getSize();
                    storageDtoParent.setSize(storageDtoParent.getSize() + sizeElementFile);
                    storageDto.setSize(storageDto.getSize() + sizeElementFile);
                    listChildrenFiles.add(buildStorageDto(elementId, storageDto, sizeElementFile));
                    break;
            }
        }
        if (storageDtoParent != null && type == SomeType.DIRECTORY) {
            storageDtoParent.setSize(storageDto.getSize() + storageDtoParent.getSize());
        }
        DirectoryStorageDto directoryStorageDtoDirectory = (DirectoryStorageDto) storageDto;
        directoryStorageDtoDirectory.setChildrenDirectories(listChildrenDirectories);
        directoryStorageDtoDirectory.setChildrenFiles(listChildrenFiles);
        return storageDto;
    }

    @Override
    public void setSizeForParents(Long size,@NonNull StorageDto storageDtoParent) throws AppException {
        Long sizeParent = storageDtoParent.getSize();
        sizeParent = sizeParent + size;
        storageDtoParent.setSize(sizeParent);
        if (storageDtoParent.getType().equals(SomeType.CONTENT)){
            throw new AppException("A different type of object was expected.", ResponseCode.TYPE_MISMATCH);
        }
        StorageDto preParent = storageDtoParent.getParent();
        if (storageDtoParent.getParent() != null) setSizeForParents(size, preParent);
    }

    @Override
    public List<StorageElement> getChildListElement(StorageElement storageElement) {
        return storageRepository.findByParent(storageElement);
    }

    @Override
    public StorageElement findById(Long id) throws AppException {
        Optional<StorageElement> foundOptional = storageRepository.findById(id);
        if (!foundOptional.isPresent()) {
            throw new AppException("By the specified parameters there is no element", ResponseCode.NO_SUCH_ELEMENT);
        }
        return foundOptional.get();
    }
}
