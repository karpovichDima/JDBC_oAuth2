package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.service.dto.response.FileDeletedDto;
import com.dazito.oauthexample.service.dto.response.FileUploadedDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public interface FileService{

    /**
     * upload multipart file
     * @param file which we want to upload on the server
     * @param parent_id this is the ID of the item in which we are putting the item(parent).
     * @return FileUploadedDto is a response object, which indicates that the file was successfully uploaded
     */
    FileUploadedDto upload(MultipartFile file, Long parent_id) throws IOException, AppException;

    /**
     * download multipart file
     * @param uuid is uuid of the file which we want to download on the client
     */
    Resource download(String uuid) throws IOException, AppException;

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
     * @param fileEntity file what been uploaded
     * @return FileUploadedDto is a response object, which indicates that the file was successfully uploaded
     */
    FileUploadedDto buildFileUploadedDto(FileEntity fileEntity);

    /**
     * set the path to the file, depending on the role of the authorized user (for administrators, the common path, for users — unique)
     * @param currentUser is user whose role we will check
     * @param uuid is uuid of the file we downloaded
     * @return Path is a path to the file
     */
    Path setFilePathDependingOnTheUserRole(AccountEntity currentUser, String uuid);

    /**
     * File search by id
     * @param id is id by which we will to find File
     * @return FileEntity
     */
    FileEntity findById(Long id);

    /**
     * File search by name
     * @param name is name by which we will to find File
     * @return FileEntity
     */
    FileEntity findByName(String name);

    /**
     * File search by uuid
     * @param uuid is uuid by which we will to find File
     * @return FileEntity
     */
    FileEntity findByUUID(String uuid) throws AppException;

    /**
     * Get file from optional if optional is not null
     * @param fileOptional this is optional, which we check for null
     * @return FileEntity
     */
    FileEntity getFileIfOptionalNotNull(Optional<FileEntity> fileOptional);

    /**
     * we load the new file on old uuid
     * @param file this is the file that we load
     * @param uuid this is the uuid to which we upload the new file
     * @return FileUploadedDto is a response object, which indicates that the file was successfully uploaded
     */
    FileUploadedDto updateFile(MultipartFile file, String uuid) throws IOException, AppException;

    /**
     * delete the file by uuid
     * @param id is uuid by which we will to delete File
     * @return FileDeletedDto is a response object, which indicates that the file was successfully deleted
     */
    FileDeletedDto delete(String id) throws IOException, AppException;
}
