package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.dto.request.StorageUpdateDto;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import com.dazito.oauthexample.service.dto.response.StorageUpdatedDto;

import java.util.List;
import java.util.Optional;

public interface StorageService {

    StorageUpdatedDto editData(StorageUpdateDto storageUpdateDto);

    StorageElement findById(Long id);

    /**
     * create hierarchy object
     * @param id of the object from which we will begin the hierarchy
     * @return new object of the hierarchy
     */
    StorageDto buildStorageDto(Long id, StorageDto storageDtoParent);

    StorageDto createHierarchy(Long id);

    void setSizeForParents(Long size, StorageDto storageDtoParent);

    List<StorageElement> getChildListElement(StorageElement storageElement);


}
