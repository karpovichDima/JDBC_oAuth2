package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreated;
import com.dazito.oauthexample.service.dto.response.FileUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileService{

    /**
     * upload multipart file
     * @param file which we want to upload on the server
     */
    FileUploadResponse upload(MultipartFile file, Long parent_id) throws IOException;

    /**
     * download multipart file
     * @param uuid is uuid of the file which we want to download on the client
     */
    ResponseEntity<Resource> download(String uuid) throws IOException;

    /**
     * check matches if of the current user and if ot the file owner
     * @param idCurrent is id of the current user
     * @param ownerId is id ot the file owner
     * @return true = if emailCurrent == ownerEmail
     */
    boolean matchesOwner(Long idCurrent, Long ownerId);

    /**
     * create single directory by path
     * @param path of the which we will create directory
     * @return new File directory
     */
    File createSinglePath(String path);

    /**
     * create multiply path directory by path
     * @param path of the which we will create directories
     * @return new File directory
     */
    File createMultiplyPath(String path);


    Content createContent(AccountEntity newUser);

    DirectoryCreated createDirectory(DirectoryDto directoryDto);


    String createHierarchy(String uuid);
}
