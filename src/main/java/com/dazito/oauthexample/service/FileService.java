package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.service.dto.response.FileDeletedDto;
import com.dazito.oauthexample.service.dto.response.FileUploadedDto;
import com.dazito.oauthexample.utils.exception.CurrentUserIsNotAdminException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public interface FileService{

    /**
     * upload multipart file
     * @param file which we want to upload on the server
     */
    FileUploadedDto upload(MultipartFile file, Long parent_id) throws IOException;

    /**
     * download multipart file
     * @param uuid is uuid of the file which we want to download on the client
     */
    ResponseEntity<Resource> download(String uuid) throws IOException, CurrentUserIsNotAdminException;

    /**
     * look for the Storage Element in different ways depending on the parameter passed
     * @param parentId this is the parent element by which we will search for the storage element
     * @param organization this is the organization by which we will search for the storage element
     * @return StorageElement is found object
     */
    StorageElement findContentDependingOnTheParent(Long parentId, Organization organization);
    /**
     * generate uuid and convert to string
     * @return String uuid
     */
    String generateStringUuid();

    /**
     * generate uuid and convert to string
     * @param fileEntity
     * @return FileUploadedDto is a response object, which indicates that the file was successfully uploaded
     */
    FileUploadedDto buildFileUploadedDto(FileEntity fileEntity);

    /**
     * generate uuid and convert to string
     * @param currentUser
     * @param uuid
     * @return FileUploadedDto is a response object, which indicates that the file was successfully uploaded
     */
    Path setFilePathDependingOnTheUserRole(AccountEntity currentUser, String uuid);

    FileEntity findById(Long id);

    FileEntity findByName(String name);

    FileEntity findByUUID(String uuid);

    FileEntity getFileIfOptionalNotNull(Optional<FileEntity> fileOptional);

    FileUploadedDto updateFile(MultipartFile file, String uuid) throws IOException;

    FileDeletedDto delete(String id) throws IOException;
}
