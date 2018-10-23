package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.FileRepository;
import com.dazito.oauthexample.dao.StorageRepository;
import com.dazito.oauthexample.model.*;
import com.dazito.oauthexample.model.type.ResponseCode;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.model.type.UserRole;
import com.dazito.oauthexample.service.*;
import com.dazito.oauthexample.service.dto.response.FileDeletedDto;
import com.dazito.oauthexample.service.dto.response.FileUploadedDto;
import com.dazito.oauthexample.utils.exception.AppException;
import liquibase.util.file.FilenameUtils;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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

    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UtilService utilService;
    @Autowired
    private ContentService contentService;


    // upload multipart file on the server
    @Override
    public FileUploadedDto upload(@NonNull MultipartFile file, Long parentId) throws IOException, AppException {
        String originalFilename = file.getOriginalFilename();

        AccountEntity currentUser = userServices.getCurrentUser();
        Organization organization = currentUser.getOrganization();
        UserRole role = currentUser.getRole();
        String rootReference = currentUser.getContent().getRoot();
        Path rootPath;

        rootPath = this.root;
        if (role == UserRole.USER) rootPath = Paths.get(rootReference);
        if (!Files.exists(rootPath)) throw new AppException("The path does not exist or has an error.", ResponseCode.PATH_NOT_EXIST);
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
        fileEntity.setOrganization(currentUser.getOrganization());

        StorageElement foundContent = findContentDependingOnTheParent(parentId, organization);

        List<StorageElement> parents = new ArrayList<>();
        parents.add(foundContent);
        fileEntity.setParents(parents);

        storageRepository.saveAndFlush(fileEntity);
        return buildFileUploadedDto(fileEntity);
    }

    // download file by uuid and response
    @Override
    public org.springframework.core.io.Resource download(String uuid) throws IOException, AppException {

        AccountEntity currentUser = userServices.getCurrentUser();
        Long idCurrent = currentUser.getId();

        StorageElement fileEntityFromDB = findByUUID(uuid);
        AccountEntity fileOwner = fileEntityFromDB.getOwner();
        Long ownerId = fileOwner.getId();

        if (!utilService.matchesOwner(idCurrent, ownerId)) {
            userServices.adminRightsCheck(currentUser);
        }
        Path filePath = setFilePathDependingOnTheUserRole(currentUser, uuid);
        if (!Files.exists(filePath)) throw new AppException("The path does not exist or has an error.", ResponseCode.PATH_NOT_EXIST);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
        return resource;
    }

    @Override
    public FileUploadedDto updateFile(@NonNull MultipartFile file, String uuid) throws IOException, AppException {
        AccountEntity currentUser = userServices.getCurrentUser();
        FileEntity foundFile = findByUUID(uuid);

        StorageElementWithChildren parentLeadingToContent = findContentParent(foundFile);

        AccountEntity owner = foundFile.getOwner();
        Organization organization = currentUser.getOrganization();

        boolean canChange = utilService.isPermissionsAdminOrUserIsOwner(currentUser, owner, foundFile);
        if (!canChange) throw new AppException("You are not allowed to change", ResponseCode.CURRENT_USER_IS_NOT_ADMIN);
        String organizationNameCurrentUser = currentUser.getOrganization().getOrganizationName();
        String organizationNameFound = foundFile.getOrganization().getOrganizationName();
        utilService.isMatchesOrganization(organizationNameCurrentUser,organizationNameFound);

        UserRole role = currentUser.getRole();
        String rootReference = currentUser.getContent().getRoot();
        Path rootPath;
        rootPath = this.root;
        if (role == UserRole.USER) rootPath = Paths.get(rootReference);
        if (!Files.exists(rootPath)) throw new AppException("The path does not exist or has an error.", ResponseCode.PATH_NOT_EXIST);

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String name = foundFile.getName();
        String pathNewFile = rootPath + File.separator + uuid;
        file.transferTo(new File(pathNewFile));
        Long size = file.getSize();

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(name);
        fileEntity.setUuid(uuid);
        fileEntity.setOwner(currentUser);
        fileEntity.setSize(size);
        fileEntity.setExtension(extension);
        fileEntity.setOrganization(currentUser.getOrganization());
        storageRepository.delete(foundFile);
        StorageElement foundStorageElement = findContentDependingOnTheParent(parentLeadingToContent.getId(), organization);

        List<StorageElement> parents = fileEntity.getParents();
        parents.add(foundStorageElement);
        fileEntity.setParents(parents);

        storageRepository.saveAndFlush(fileEntity);

        return buildFileUploadedDto(fileEntity);
    }

    private StorageElementWithChildren findContentParent(FileEntity foundFile) throws AppException {
        StorageElementWithChildren parent;
        List<StorageElement> parents = foundFile.getParents();
        for (StorageElement element : parents) {
            if (element.getType() == SomeType.CONTENT){
                parent = (StorageElementWithChildren) element;
                return parent;
            }
            StorageElementWithChildren storageElementWithChildren = recursForFindContentParent(element);
            if (storageElementWithChildren != null) return (StorageElementWithChildren) element;
        }
        throw new AppException("Content for object not exist", ResponseCode.CONTENT_NOT_EXIST);
    }

    private StorageElementWithChildren recursForFindContentParent(StorageElement transferElement) throws AppException {
        StorageElementWithChildren parent;
        List<StorageElement> parents = transferElement.getParents();
        for (StorageElement element : parents) {
            if (element.getType() == SomeType.CONTENT){
                parent = (StorageElementWithChildren) element;
                return parent;
            } else {
                recursForFindContentParent(element);
            }
        }
        return null;
    }

    @Override
    public FileDeletedDto delete(String uuid) throws IOException, AppException {
        AccountEntity currentUser = userServices.getCurrentUser();
        StorageElement foundStorage = findByUUID(uuid);
        AccountEntity owner = foundStorage.getOwner();
        SomeType type = foundStorage.getType();
        UserRole role = currentUser.getRole();

        boolean canChange = utilService.isPermissionsAdminOrUserIsOwner(currentUser, owner, foundStorage);
        if (!canChange) throw new AppException("You are not allowed to change.", ResponseCode.CURRENT_USER_IS_NOT_ADMIN);
        String organizationNameCurrentUser = currentUser.getOrganization().getOrganizationName();
        String organizationNameFound = foundStorage.getOrganization().getOrganizationName();
        utilService.isMatchesOrganization(organizationNameCurrentUser,organizationNameFound);

        if (!type.equals(SomeType.FILE))
            throw new AppException("A different type of object was expected.", ResponseCode.TYPE_MISMATCH);
        storageRepository.delete(foundStorage);

        Path rootContent;
        if (role.equals(UserRole.USER)) {
            rootContent = Paths.get(currentUser.getContent().getRoot());
        } else {
            rootContent = root;
        }
        Path pathFile = Paths.get(rootContent + File.separator + uuid);
        Files.delete(pathFile);

        FileDeletedDto fileDeletedResponseDto = new FileDeletedDto();
        fileDeletedResponseDto.setUuid(uuid);
        fileDeletedResponseDto.setId(foundStorage.getId());
        fileDeletedResponseDto.setName(foundStorage.getName());

        return fileDeletedResponseDto;
    }

    @Override
    public StorageElement findContentDependingOnTheParent(Long parentId, Organization organization) {
        StorageElement foundParent;
        if (parentId != 0) {
            foundParent = storageRepository.findById(parentId).get();
            return  foundParent;
        } else {
            AccountEntity currentUser = userServices.getCurrentUser();
            foundParent = currentUser.getContent();
            return foundParent;
        }
    }

    @Override
    public String generateStringUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid + "";
    }

    @Override
    public FileUploadedDto buildFileUploadedDto(FileEntity fileEntity) {
        String name = fileEntity.getName();
        String extension = fileEntity.getExtension();
        Long size = fileEntity.getSize();
        String uuid = fileEntity.getUuid();

        FileUploadedDto fileUploadedDto = new FileUploadedDto();
        fileUploadedDto.setName(name + "." + extension);
        fileUploadedDto.setSize(size);
        fileUploadedDto.setReferenceToDownloadFile(downloadPath + uuid);

        return fileUploadedDto;
    }

    @Override
    public Path setFilePathDependingOnTheUserRole(AccountEntity currentUser, String uuid) {
        UserRole role = currentUser.getRole();
        Path filePath;
        if (role.equals(UserRole.USER)) {
            filePath = Paths.get(currentUser.getContent().getRoot(), uuid);
        } else {
            filePath = Paths.get(root.toString(), uuid);
        }
        return filePath;
    }

    @Override
    public FileEntity findByName(String name) {
        Optional<FileEntity> fileOptional = fileRepository.findByName(name);
        return getFileIfOptionalNotNull(fileOptional);
    }

    @Override
    public FileEntity findByUUID(String uuid) throws NoSuchElementException, AppException {
        Optional<FileEntity> fileOptional = fileRepository.findByUuid(uuid);
        if (!fileOptional.isPresent()) throw new AppException("File by uuid not found", ResponseCode.NO_SUCH_ELEMENT);
        return fileOptional.get();
    }

    @Override
    public FileEntity findById(Long id) throws NoSuchElementException{
        return fileRepository.findById(id).get();
    }

    @Override
    public FileEntity getFileIfOptionalNotNull(Optional<FileEntity> fileOptional) {
        boolean checkOnNull = userServices.isOptionalNotNull(fileOptional);
        if (!checkOnNull) return null;
        return fileOptional.get();
    }

}
