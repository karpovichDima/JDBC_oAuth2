package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.SomeType;
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

    Content createContent(AccountEntity newUser);

    DirectoryCreated createDirectory(DirectoryDto directoryDto);

    StorageElement findStorageElementDependingOnTheParent(Long parentId);

    String generateStringUuid();

    FileUploadResponse responseFileUploaded(FileEntity fileEntity);

    Path setFilePathDependingOnTheUserRole(AccountEntity currentUser, String uuid);

    /**
     * create single directory by path
     * @param path of the which we will create directory
     * @return new File directory
     */
    File createSinglePath(String path);

    StorageDto buildStorageDto(Long id);

    StorageElement findByIdInStorageRepo(Long id);

    StorageElement findByNameInStorageRepo(String name);

    FileEntity findByUUIDInFileRepo(String uuid);

    StorageElement getStorageIfOptionalNotNull(Optional<StorageElement> storageOptional);

    FileEntity getFileIfOptionalNotNull(Optional<FileEntity> fileOptional);

    List<StorageElement> getChildListElement(StorageElement storageElement);

    List<StorageDto> getListChildrenFromElementChildrenDependingOnType(SomeType someType,
                                                                       List<StorageElement> elementChildren);
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
