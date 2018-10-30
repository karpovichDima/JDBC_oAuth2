package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.ChannelRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.ResponseCode;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.service.ChannelService;
import com.dazito.oauthexample.service.StorageService;
import com.dazito.oauthexample.service.StructureService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.CreateSomeStructureDto;
import com.dazito.oauthexample.service.dto.request.StorageAddToSomeStructureDto;
import com.dazito.oauthexample.service.dto.response.DeletedStorageDto;
import com.dazito.oauthexample.service.dto.response.SomeStructureCreatedDto;
import com.dazito.oauthexample.service.dto.response.StorageAddedToSomeStructureDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StructureServiceImpl implements StructureService {

    @Autowired
    StorageService storageService;
    @Autowired
    UserService userService;
    @Autowired
    StorageRepository storageRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    ChannelService channelService;


    @Override
    @Transactional
    public StorageAddedToSomeStructureDto addStorageToSomeStructure(StorageAddToSomeStructureDto storageAddToSomeStructureDto) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        Long idStorage = storageAddToSomeStructureDto.getIdStorage();
        StorageElement foundStorageElement = storageService.findById(idStorage);
        SomeType typeFoundStorage = foundStorageElement.getType();
        switch (typeFoundStorage) {
            case COLLECTION:
                userService.userRightsCheck(currentUser);
                break;
            case CHANNEL:
                userService.adminRightsCheck(currentUser);
                break;
        }
        String organizationNameFoundStorage = foundStorageElement.getOrganization().getOrganizationName();
        userService.isMatchesOrganization(organizationNameFoundStorage, currentUser);

        Long idStructure = storageAddToSomeStructureDto.getIdStructure();
        StorageElementWithChildren foundStorage = (StorageElementWithChildren)storageService.findById(idStructure);

        List<StorageElement> parentsStorageElement = foundStorageElement.getParents();
        parentsStorageElement.add(foundStorage);
        foundStorageElement.setParents(parentsStorageElement);

        storageRepository.saveAndFlush(foundStorageElement);

        StorageAddedToSomeStructureDto storageAddedToSomeStructureDto = new StorageAddedToSomeStructureDto();
        storageAddedToSomeStructureDto.setIdStructure(idStructure);
        storageAddedToSomeStructureDto.setIdStorage(idStorage);
        return storageAddedToSomeStructureDto;
    }

    @Override
    @Transactional
    public SomeStructureCreatedDto createSomeStructure(CreateSomeStructureDto dto) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        SomeType typeCreateStructure = dto.getTypeCreatedStructure();
        StorageElementWithChildren structure = null;
        switch (typeCreateStructure) {
            case CHANNEL:
                userService.adminRightsCheck(currentUser);
                structure = new Channel();
                break;
            case COLLECTION:
                userService.userRightsCheck(currentUser);
                structure = new Collection();
                break;
        }
        StorageElementWithChildren recordedStructure = writeInStructure(structure, dto, currentUser);
        if (typeCreateStructure == SomeType.COLLECTION){
            List<Collection> collections = currentUser.getCollections();
            if (collections == null) collections = new ArrayList<>();
            collections.add((Collection) recordedStructure);
            currentUser.setCollections(collections);
            saveStructure(recordedStructure);
            userService.saveAccount(currentUser);
            return responseStructureCreatedDto(recordedStructure);
        }

        saveStructure(recordedStructure);
        return responseStructureCreatedDto(recordedStructure);
    }

    private SomeStructureCreatedDto responseStructureCreatedDto(StorageElementWithChildren structure) {
        SomeStructureCreatedDto someStructureCreatedDto = new SomeStructureCreatedDto();
        someStructureCreatedDto.setChannelName(structure.getName());
        StorageElement foundElement = storageRepository.findByName(structure.getName()).get();
        someStructureCreatedDto.setId(foundElement.getId());
        return someStructureCreatedDto;
    }

    private void saveStructure(StorageElement structure) {
        storageRepository.saveAndFlush(structure);
    }

    private StorageElementWithChildren writeInStructure(StorageElementWithChildren structure, CreateSomeStructureDto dto, AccountEntity currentUser) {
        String structureName = dto.getStructureName();
        structure.setName(structureName);
        structure.setOwner(currentUser);
        ArrayList<AccountEntity> listAccount = new ArrayList<>();
        structure.setListOwners(listAccount);
        ArrayList<StorageElement> listFiles = new ArrayList<>();
        structure.setParents(listFiles);
        structure.setOrganization(currentUser.getOrganization());
        return structure;
    }
}
