package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreated;
import com.dazito.oauthexample.service.dto.response.FileUploadResponse;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
     * create root point for all user directories
     * @param newUser the user for which we will create the root point
     * @return Content is root point object
     */
    Content createContent(AccountEntity newUser);

    /**
     * create Directory
     * @param directoryDto is the object from which we take the folder name and the parent element
     * @return DirectoryCreated is a response object, which indicates that the directory was successfully created
     */
    DirectoryCreated createDirectory(DirectoryDto directoryDto);

    /**
     * look for the Storage Element in different ways depending on the parameter passed
     * @param parentId this is the parent element by which we will search for the storage element
     * @return StorageElement is found object
     */
    StorageElement findStorageElementDependingOnTheParent(Long parentId);

    /**
     * generate uuid and convert to string
     * @return String uuid
     */
    String generateStringUuid();

    /**
     * generate uuid and convert to string
     * @param fileEntity
     * @return FileUploadResponse is a response object, which indicates that the file was successfully uploaded
     */
    FileUploadResponse responseFileUploaded(FileEntity fileEntity);

    Path setFilePathDependingOnTheUserRole(AccountEntity currentUser, String uuid);

    /**
     * create single directory by path
     * @param path of the which we will create directory
     * @return new File directory
     */
    File createSinglePath(String path);

    StorageDto buildStorageDto(Long id);

    StorageDto createHierarchy(Long id);

    StorageElement findByIdInStorageRepo(Long id);

    StorageElement findByNameInStorageRepo(String name);

    FileEntity findByUUIDInFileRepo(String uuid);

    StorageElement getStorageIfOptionalNotNull(Optional<StorageElement> storageOptional);

    FileEntity getFileIfOptionalNotNull(Optional<FileEntity> fileOptional);

    List<StorageElement> getChildListElement(StorageElement storageElement);

    /**
     * check matches if of the current user and if ot the file owner
     * @param idCurrent is id of the current user
     * @param ownerId is id ot the file owner
     * @return true = if emailCurrent == ownerEmail
     */
    boolean matchesOwner(Long idCurrent, Long ownerId);

    DirectoryCreated responseDirectoryCreated(Directory directory);

    File createMultiplyPath(String path);











}
