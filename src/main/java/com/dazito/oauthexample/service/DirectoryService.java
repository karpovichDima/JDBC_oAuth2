package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.Directory;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.dto.response.DirectoryDeletedDto;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;
import com.dazito.oauthexample.utils.exception.CurrentUserIsNotAdminException;
import com.dazito.oauthexample.utils.exception.EmailIsNotMatchesException;
import com.dazito.oauthexample.utils.exception.OrganizationIsNotMuchException;

import java.util.List;

public interface DirectoryService {
    /**
     * create Directory
     * @param directoryDto is the object from which we take the folder name and the parent element
     * @return DirectoryCreatedDto is a response object, which indicates that the directory was successfully created
     */
    DirectoryCreatedDto createDirectory(DirectoryDto directoryDto) throws EmailIsNotMatchesException;

    DirectoryCreatedDto responseDirectoryCreated(Directory directory);

    DirectoryCreatedDto updateDirectory(DirectoryDto directoryDto) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException;

    DirectoryDeletedDto delete(Long id) throws CurrentUserIsNotAdminException, OrganizationIsNotMuchException;

    void deleteChildFiles(List<StorageElement> listChildToDelete, List<StorageElement> listChildrenFoundStorage);
}
