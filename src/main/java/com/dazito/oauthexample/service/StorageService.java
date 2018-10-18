package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.dto.request.StorageUpdateDto;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import com.dazito.oauthexample.service.dto.response.StorageUpdatedDto;
import com.dazito.oauthexample.utils.exception.AppException;

import java.util.List;

public interface StorageService {

    /**
     * Transfer directory from one parent to another. Name, uuid, root change.
     * @param storageUpdateDto the the entity from which we take data to change
     * @return StorageUpdatedDto is response, about successful operation
     */
    StorageUpdatedDto editData(StorageUpdateDto storageUpdateDto) throws AppException;

    /**
     * StorageElement search by id
     * @param id is id by which we will to find StorageElement
     * @return StorageElement is root point object
     */
    StorageElement findById(Long id) throws AppException;

    /**
     * create hierarchy object
     * @param id of the object from which we will begin the hierarchy
     * @param storageDtoParent dto of the parent
     * @param sizeFile is size of the file child + size current object
     * @return new object of the hierarchy
     */
    StorageDto buildStorageDto(Long id, StorageDto storageDtoParent, long sizeFile) throws AppException;

    /**
     * create hierarchy object(recursion)
     * @param id of the object from which we will begin the hierarchy
     * @return new object of the hierarchy
     */
    StorageDto createHierarchy(Long id) throws AppException;

    /**
     * irrational way of calculating the size of the hierarchy
     * @param size
     * @param storageDtoParent
     */
    void setSizeForParents(Long size, StorageDto storageDtoParent) throws AppException;

    /**
     * get child list item
     * @param storageElement is item whose list of children we will receive
     * @return child list item
     */
    List<StorageElement> getChildListElement(StorageElement storageElement);


}
