package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.FileRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreated;
import com.dazito.oauthexample.service.dto.response.FileUploadResponse;
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {


    @Autowired
    StorageRepository storageRepository;

    @Autowired
    FileRepository fileRepository;

    @Resource(name = "userService")
    UserService userServices;

    @Value("${root.path}")
    Path root;

    @Value("${path.downloadFile}")
    String downloadPath;

    @Value("${content.admin}")
    String contentName;

    // upload multipart file on the server
    @Override
    public FileUploadResponse upload(MultipartFile file, Long parentId) throws IOException {
        if (file == null) return null;

        AccountEntity currentUser = userServices.getCurrentUser();
        Path rootPath;

        switch (currentUser.getRole()) {
            case USER:
                rootPath = Paths.get(currentUser.getContent().getRoot());
                break;
            case ADMIN:
                rootPath = root;
                break;
            default:
                rootPath = root;
        }


        if (!Files.exists(rootPath)) return null;

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = FilenameUtils.getBaseName(file.getOriginalFilename());

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid + "";

        file.transferTo(new File(rootPath + File.separator + uuid));

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(name);
        fileEntity.setUuid(uuidString);
        fileEntity.setOwner(userServices.getCurrentUser());
        fileEntity.setSize(file.getSize());
        fileEntity.setExtension(extension);

        Optional<StorageElement> byId;
        if (parentId == 0) {
            byId = storageRepository.findByName("CONTENT");
        } else {
            byId = storageRepository.findById(parentId);
        }

        if (!userServices.checkOptionalOnNull(byId)) return null;
        StorageElement storageElement = byId.get();
        fileEntity.setParentId(storageElement);
        storageRepository.saveAndFlush(fileEntity);

        return responseFileUploaded(fileEntity);
    }

    private FileUploadResponse responseFileUploaded(FileEntity fileEntity) {
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setName(fileEntity.getName() + "." + fileEntity.getExtension());
        fileUploadResponse.setSize(fileEntity.getSize());
        fileUploadResponse.setReferenceToDownloadFile(downloadPath + fileEntity.getUuid());

        return fileUploadResponse;
    }

    // download file by uuid and response
    @Override
    public ResponseEntity<org.springframework.core.io.Resource> download(String uuid) throws IOException {

        AccountEntity currentUser = userServices.getCurrentUser();
        Long idCurrent = currentUser.getId();

        Optional<FileEntity> byfileUUID = fileRepository.findByUuid(uuid);
        boolean checkedOnNull = userServices.checkOptionalOnNull(byfileUUID);
        if (!checkedOnNull) return null;

        FileEntity fileEntity = byfileUUID.get();

        if (!matchesOwner(idCurrent, fileEntity.getOwner().getId())) {
            if (!userServices.adminRightsCheck(currentUser)) return null;
            // user is not admin and not owner of the file
        }

        Path file;

        switch (currentUser.getRole()) {
            case USER:
                file = Paths.get(currentUser.getContent().getRoot(), uuid);
                break;
            case ADMIN:
                file = Paths.get(root.toString(), uuid);
                break;
            default:
                file = Paths.get(root.toString(), uuid);
        }

        if (!Files.exists(file)) return null;

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(file));

        return ResponseEntity.ok().body(resource);
    }

    // create single directory
    @Override
    public File createSinglePath(String path) {
        File rootPath = new File(path);
        if (!rootPath.exists()) {
            if (rootPath.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
        return rootPath;
    }

    // create multiply path directory
    @Override
    public File createMultiplyPath(String path) {
        File rootPath2 = new File(path + "\\Directory\\Sub\\Sub-Sub");
        if (!rootPath2.exists()) {
            if (rootPath2.mkdirs()) {
                System.out.println("Multiple directories are created!");
            } else {
                System.out.println("Failed to create multiple directories!");
            }
        }
        return rootPath2;
    }

    @Override
    public Content createContent(AccountEntity newUser) {

        UserRole role = newUser.getRole();
        String nameNewFolder = newUser.getEmail();

        Content content = new Content();

        switch (role) {
            case USER:
                content.setName("Content " + newUser.getEmail());
                createSinglePath(root + File.separator + File.separator + nameNewFolder);
                content.setRoot(root + File.separator + File.separator + nameNewFolder);
                break;
            case ADMIN:
                content.setName(contentName);
                content.setRoot(root.toString());
                break;
        }
        content.setOwner(newUser);
        content.setParentId(null);

        return content;
    }

    @Override
    public DirectoryCreated createDirectory(DirectoryDto directoryDto) {
        String name = directoryDto.getName();
        Long parent_id = directoryDto.getParentId();

        if (parent_id == 0) {
            // find Content
            Optional<StorageElement> content = storageRepository.findByName("CONTENT");
            if (!userServices.checkOptionalOnNull(content)) return null;
            StorageElement parent = content.get();

            Directory directory = new Directory();
            directory.setName(name);
            directory.setParentId(parent);
            directory.setOwner(userServices.getCurrentUser());

            storageRepository.saveAndFlush(directory);

            return responseDirectoryCreated(directory);
        } else {
            // find parent
            Optional<StorageElement> parent = storageRepository.findById(parent_id);
            if (!userServices.checkOptionalOnNull(parent)) return null;
            StorageElement parentStorage = parent.get();

            // check parent on type DIRECTORY or CONTENT
            if (parentStorage.getType().equals("FILE")) return null;

            Directory directory = new Directory();
            directory.setName(name);
            directory.setParentId(parentStorage);
            directory.setOwner(userServices.getCurrentUser());

            storageRepository.saveAndFlush(directory);

            return responseDirectoryCreated(directory);
        }
    }

    // check matches id of the current user and id ot the file owner
    @Override
    public boolean matchesOwner(Long idCurrent, Long ownerId) {
        if (idCurrent == ownerId) return true;
        return false;
    }

    private DirectoryCreated responseDirectoryCreated(Directory directory) {
        DirectoryCreated directoryResponseDto = new DirectoryCreated();
        directoryResponseDto.setName(directory.getName());
        directoryResponseDto.setParentId(directory.getParentId().getId());

        return directoryResponseDto;
    }


}
