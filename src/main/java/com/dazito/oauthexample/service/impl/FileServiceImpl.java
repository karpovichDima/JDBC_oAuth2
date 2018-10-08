package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.FileRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreated;
import com.dazito.oauthexample.service.dto.response.FileUploadResponse;
import com.dazito.oauthexample.service.dto.response.StorageDto;
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
import java.util.*;

@Service
public class FileServiceImpl implements FileService {

    @Value("${root.path}")
    Path root;
    @Value("${path.downloadFile}")
    String downloadPath;
    @Value("${content.admin}")
    String contentName;
    @Resource(name = "userService")
    UserService userServices;

    private final StorageRepository storageRepository;
    private final FileRepository fileRepository;

    @Autowired
    public FileServiceImpl(StorageRepository storageRepository, FileRepository fileRepository) {
        this.storageRepository = storageRepository;
        this.fileRepository = fileRepository;
    }

    // upload multipart file on the server
    @Override
    public FileUploadResponse upload(MultipartFile file, Long parentId) throws IOException {
        if (file == null) return null;

        String originalFilename = file.getOriginalFilename();

        AccountEntity currentUser = userServices.getCurrentUser();
        UserRole role = currentUser.getRole();
        String rootReference = currentUser.getContent().getRoot();
        Path rootPath;

        rootPath = this.root;
        if (role == UserRole.USER) rootPath = Paths.get(rootReference);
        if (!Files.exists(rootPath)) return null;

        String extension = FilenameUtils.getExtension(originalFilename);
        String name = FilenameUtils.getBaseName(originalFilename);

        String uuidString = generateStringUuid();

        String pathNewFile = rootPath + File.separator + uuidString;
        file.transferTo(new File(pathNewFile));

        Long size = file.getSize();

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(name);
        fileEntity.setUuid(uuidString);
        fileEntity.setOwner(currentUser);
        fileEntity.setSize(size);
        fileEntity.setExtension(extension);

        StorageElement foundStorageElement = findStorageElementDependingOnTheParent(parentId);

        fileEntity.setParentId(foundStorageElement);

        setSizeForParents(size, parentId);

        storageRepository.saveAndFlush(fileEntity);

        return responseFileUploaded(fileEntity);
    }

    private void setSizeForParents(Long size, Long parentId) {

        StorageElement parent = findByIdInStorageRepo(parentId);
        Long sizeParent = parent.getSize();
        sizeParent = sizeParent + size;
        parent.setSize(sizeParent);
        storageRepository.saveAndFlush(parent);
        if (parent.getType().equals(SomeType.CONTENT)) return;
        Long idParentParent = parent.getParentId().getId();
        if (parent.getParentId() != null) setSizeForParents(size, idParentParent);
    }

    // download file by uuid and response
    @Override
    public ResponseEntity<org.springframework.core.io.Resource> download(String uuid) throws IOException {

        AccountEntity currentUser = userServices.getCurrentUser();
        Long idCurrent = currentUser.getId();

        StorageElement fileEntityFromDB = findByUUIDInFileRepo(uuid);
        AccountEntity fileOwner = fileEntityFromDB.getOwner();
        Long ownerId = fileOwner.getId();

        if (!matchesOwner(idCurrent, ownerId)) {
            if (!userServices.adminRightsCheck(currentUser)) return null; // user is not admin and not owner of the file
        }

        Path filePath = setFilePathDependingOnTheUserRole(currentUser, uuid);

        if (!Files.exists(filePath)) return null;

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));

        return ResponseEntity.ok().body(resource);
    }

    // create root for all directories and files(for Admins) or for one User
    @Override
    public Content createContent(AccountEntity newUser) {

        UserRole role = newUser.getRole();
        String nameNewFolder = newUser.getEmail();

        Content content = new Content();

        switch (role) {
            case USER:
                content.setName("Content " + newUser.getEmail());
                createSinglePath(root + File.separator + nameNewFolder);
                content.setRoot(root + File.separator + nameNewFolder);
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

    // create new Directory by parent id and name
    @Override
    public DirectoryCreated createDirectory(DirectoryDto directoryDto) {
        AccountEntity currentUser = userServices.getCurrentUser();
        String name = directoryDto.getName();
        Long parent_id = directoryDto.getParentId();

        StorageElement foundParentElement;

        Directory directory = new Directory();
        directory.setName(name);

        if (parent_id == 0) {
            foundParentElement = findByNameInStorageRepo("CONTENT");
        } else {
            foundParentElement = findByIdInStorageRepo(parent_id);
            SomeType type = foundParentElement.getType();
            if (type.equals(SomeType.FILE)) return null;
        }

        directory.setParentId(foundParentElement);
        directory.setOwner(currentUser);

        storageRepository.saveAndFlush(directory);

        return responseDirectoryCreated(directory);
    }

    @Override
    public StorageElement findStorageElementDependingOnTheParent(Long parentId) {
        StorageElement foundStorageElement;
        if (parentId != 0){
            foundStorageElement = findByIdInStorageRepo(parentId);
        }else{
            foundStorageElement = findByNameInStorageRepo("CONTENT");
        }
        return foundStorageElement;
    }

    @Override
    public String generateStringUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid + "";
}

    @Override
    public FileUploadResponse responseFileUploaded(FileEntity fileEntity) {
        String name = fileEntity.getName();
        String extension = fileEntity.getExtension();
        Long size = fileEntity.getSize();
        String uuid = fileEntity.getUuid();

        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setName(name + "." + extension);
        fileUploadResponse.setSize(size);
        fileUploadResponse.setReferenceToDownloadFile(downloadPath + uuid);

        return fileUploadResponse;
    }

    @Override
    public Path setFilePathDependingOnTheUserRole(AccountEntity currentUser, String uuid) {
        UserRole role = currentUser.getRole();
        Path filePath;
        if (role.equals(UserRole.USER)){
            filePath = Paths.get(currentUser.getContent().getRoot(), uuid);
        } else {
            filePath = Paths.get(root.toString(), uuid);
        }
        return filePath;
    }

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

    @Override
    public StorageDto createHierarchy(Long id){
        StorageDto storageDto = buildStorageDto(id);

        return storageDto;
    }

    @Override
    public StorageDto buildStorageDto(Long id) {
        StorageElement storageElement = findByIdInStorageRepo(id);

        Long idElement = storageElement.getId();
        String nameElement = storageElement.getName();
        SomeType typeElement = storageElement.getType();
        Long size = storageElement.getSize();

        StorageDto storageDto = new StorageDto();

        storageDto.setId(idElement);
        storageDto.setName(nameElement);
        storageDto.setType(typeElement);
        storageDto.setSize(size);

        List<StorageElement> elementChildren = getChildListElement(storageElement);
        List<StorageDto> listChildren = createListChildrenFromElementChildren(elementChildren);

        storageDto.setChildren(listChildren);

        return storageDto;
    }

    @Override
    public List<StorageDto> createListChildrenFromElementChildren(List<StorageElement> elementChildren) {
        List<StorageDto> listChildren = new ArrayList<>();
        for (StorageElement element : elementChildren) {
            long elementId = element.getId();
            listChildren.add(buildStorageDto(elementId));
        }
        return listChildren;
    }

    @Override
    public StorageElement findByIdInStorageRepo(Long id) {
        Optional<StorageElement> storageOptional = storageRepository.findById(id);
        return getStorageIfOptionalNotNull(storageOptional);
    }

    @Override
    public StorageElement findByNameInStorageRepo(String name) {
        Optional<StorageElement> storageOptional = storageRepository.findByName(name);
        return getStorageIfOptionalNotNull(storageOptional);
    }

    @Override
    public FileEntity findByUUIDInFileRepo(String uuid) {
        Optional<FileEntity> storageOptional = fileRepository.findByUuid(uuid);
        return getFileIfOptionalNotNull(storageOptional);
    }

    @Override
    public StorageElement getStorageIfOptionalNotNull(Optional<StorageElement> storageOptional){
        boolean checkOnNull = userServices.checkOptionalOnNull(storageOptional);
        if (!checkOnNull) return null;
        return storageOptional.get();
    }

    @Override
    public FileEntity getFileIfOptionalNotNull(Optional<FileEntity> fileOptional){
        boolean checkOnNull = userServices.checkOptionalOnNull(fileOptional);
        if (!checkOnNull) return null;
        return fileOptional.get();
    }

    @Override
    public List<StorageElement> getChildListElement(StorageElement storageElement) {
        return storageRepository.findByParentId(storageElement);
    }

    @Override
    public boolean matchesOwner(Long idCurrent, Long ownerId) {
        return Objects.equals(idCurrent, ownerId);
    }

    @Override
    public DirectoryCreated responseDirectoryCreated(Directory directory) {
        String nameDir = directory.getName();
        StorageElement parentDir = directory.getParentId();
        Long idDir = parentDir.getId();

        DirectoryCreated directoryResponseDto = new DirectoryCreated();

        directoryResponseDto.setName(nameDir);
        directoryResponseDto.setParentId(idDir);

        return directoryResponseDto;
    }

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
}
