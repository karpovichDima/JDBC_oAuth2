package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.Directory;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreatedDto;

public interface DirectoryService {
    /**
     * create Directory
     * @param directoryDto is the object from which we take the folder name and the parent element
     * @return DirectoryCreatedDto is a response object, which indicates that the directory was successfully created
     */
    DirectoryCreatedDto createDirectory(DirectoryDto directoryDto);

    DirectoryCreatedDto responseDirectoryCreated(Directory directory);

    DirectoryCreatedDto updateDirectory(DirectoryDto directoryDto);
}
