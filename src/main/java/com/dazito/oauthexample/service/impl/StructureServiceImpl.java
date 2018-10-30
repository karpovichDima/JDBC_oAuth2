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
        userService.adminRightsCheck(currentUser);
        Long idStorage = storageAddToSomeStructureDto.getIdStorage();
        StorageElement foundStorageElement = storageService.findById(idStorage);
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
        userService.adminRightsCheck(currentUser);
        SomeType typeCreateStructure = dto.getTypeCreatedStructure();
        StorageElementWithChildren structure = null;
        switch (typeCreateStructure) {
            case CHANNEL:
                structure = new Channel();
                break;
            case COLLECTION:
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

    @Override
    @Transactional
    public DeletedStorageDto deleteStorageFromStructure(Long idStructure, Long idStorage) throws AppException {
        AccountEntity currentUser = userService.getCurrentUser();
        StorageElement foundStorageElement = storageService.findById(idStorage);
        String organizationNameFoundStorage = foundStorageElement.getOrganization().getOrganizationName();
        userService.isMatchesOrganization(organizationNameFoundStorage, currentUser);

        StorageElementWithChildren foundStructure = (StorageElementWithChildren)storageService.findById(idStructure);
        SomeType structureType = foundStructure.getType();
        switch (structureType) {
            case COLLECTION:
                break;
            case CHANNEL:
                checkWhetherPartOfTheChannel(foundStructure, foundStorageElement);
                DeletedStorageDto deletedStorageDto = deleteStorageIfItFile(foundStorageElement, foundStorageElement);
                if (deletedStorageDto == null)deleteStorageIfItDirectory((StorageElementWithChildren) foundStorageElement, (StorageElementWithChildren) foundStorageElement);
                break;
        }
        return responseStorageDeleteFromChannel();
    }

    private void deleteStorageIfItDirectory(StorageElementWithChildren foundStructure, StorageElementWithChildren foundStorageElement) {
        List<StorageElement> listToDelete = new ArrayList<>();
        recursionForCreateListToDelete(listToDelete, foundStructure, foundStorageElement);
        deleteStorageFromParents(listToDelete, (Channel) foundStructure);
        removeTopmostItem(foundStorageElement);
    }

    private DeletedStorageDto deleteStorageIfItFile(StorageElement foundStorageElement, StorageElement foundStructure) throws AppException {
        if(!(foundStorageElement instanceof StorageElementWithChildren)){
            List<StorageElement> parents = foundStorageElement.getParents();
            for (StorageElement parent : parents) {
                boolean isPartChannel = channelService.checkStorageOnChannel((Channel) foundStructure, parent);
                if (!isPartChannel) continue;
                StorageElementWithChildren castedParent = (StorageElementWithChildren) parent;
                List<StorageElement> children = castedParent.getChildren();
                children.remove(foundStorageElement);
                saveStructure(foundStructure);            }
            return responseStorageDeleteFromChannel();
        }
        return null;
    }

    private void checkWhetherPartOfTheChannel(StorageElementWithChildren foundStructure, StorageElement foundStorageElement) throws AppException {
        boolean onChannel = channelService.checkStorageOnChannel((Channel) foundStructure, foundStorageElement);
        if (!onChannel)
            throw new AppException("Storage element is not exist on channel.", ResponseCode.NO_SUCH_ELEMENT);
    }

    private DeletedStorageDto responseStorageDeleteFromChannel() {
        DeletedStorageDto deletedStorageDto = new DeletedStorageDto();
        deletedStorageDto.setNameDeletedStorage("Deleted");
        return deletedStorageDto;
    }

    private void removeTopmostItem(StorageElement foundStorageElement) {
        List<StorageElement> parents = foundStorageElement.getParents();
        for (StorageElement element : parents) {
            StorageElementWithChildren castedElement = (StorageElementWithChildren) element;
            List<StorageElement> children = castedElement.getChildren();
            children.remove(foundStorageElement);
        }
    }

    private void deleteStorageFromParents(List<StorageElement> listToDelete, Channel foundChannel) {
        for (StorageElement element : listToDelete) {
            List<StorageElement> parents = element.getParents();
            raiseEachParent(parents, element, foundChannel);
        }
    }

    private void raiseEachParent(List<StorageElement> parents, StorageElement foundStorageElement, Channel foundChannel) {
        for (StorageElement parent : parents) {
            // если объект ведет к Channel, который мы указали, тогда выполняем код дальше(он удаляется)
            // иначе пропускаем итерацию
            boolean partChannel = channelService.isPartChannel(parent, foundChannel);
            if (!partChannel) continue;
            StorageElementWithChildren castParent = (StorageElementWithChildren) parent;
            List<StorageElement> children = castParent.getChildren();
            children.remove(foundStorageElement);
        }
    }

    private void recursionForCreateListToDelete(List<StorageElement> listToDelete, StorageElement foundChannel, StorageElement foundStorageElement) {
        List<StorageElement> children = null;
        if (foundStorageElement instanceof StorageElementWithChildren) {
            StorageElementWithChildren castStorage = (StorageElementWithChildren)foundStorageElement;
            children = castStorage.getChildren();
            if (children.isEmpty()) return;
        }
        for (StorageElement child : children) {
            boolean partChannel = channelService.isPartChannel(child, foundChannel);
            if (partChannel) {
                listToDelete.add(child);
                if (child instanceof StorageElementWithChildren) {
                    recursionForCreateListToDelete(listToDelete, foundChannel, child);
                }
            }
        }
    }

}
