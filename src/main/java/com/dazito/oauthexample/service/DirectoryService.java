package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.Directory;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.dto.response.DirectoryDeletedDto;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import com.dazito.oauthexample.utils.exception.AppException;

import java.util.List;

public interface DirectoryService {

    /**
     * create Directory
     * @param directoryDto is the object from which we take the folder name and the parent element
     * @return DirectoryCreatedDto is a response object, which indicates that the directory was successfully created
     */
    DirectoryCreatedDto createDirectory(DirectoryDto directoryDto) throws AppException;

    /**
     * response that says successful directory creation
     * @param directory the directory that was created
     * @return DirectoryCreatedDto is response-object
     */
    DirectoryCreatedDto responseDirectoryCreated(Directory directory);

    /**
     * Transfer directory from one parent to another. Name change.
     * @param directoryDto the the entity from which we take data to change
     * @return DirectoryCreatedDto is response, about successful operation
     */
    DirectoryCreatedDto updateDirectory(DirectoryDto directoryDto) throws AppException;

    /**
     * delete directory by id
     * @param id is id by which we will to delete directory
     * @return DirectoryDeletedDto is response, about successful operation
     */
    DirectoryDeletedDto delete(Long id) throws AppException;

    /**
     * remove all children from content
     * @param listChildToDelete this is a list of all children content
     * @param listChildrenFoundStorage this is a list of children content(first level)
     */
    void deleteChildFiles(List<StorageElement> listChildToDelete, List<StorageElement> listChildrenFoundStorage);
}
