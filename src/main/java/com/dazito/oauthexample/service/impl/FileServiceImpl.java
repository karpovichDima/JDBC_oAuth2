package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.ContentRepository;
import com.dazito.oauthexample.dao.FileEntityRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Content;
import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.model.type.SomeType;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.UserService;
import liquibase.util.file.FilenameUtils;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {


    @Autowired
    FileEntityRepository fileEntityRepository;

    @Autowired
    ContentRepository contentRepository;

    @Resource(name = "userService")
    UserService userServices;

    @Value("${root.path}")
    String root;

    // upload multipart file on the server
    @Override
    public void upload(MultipartFile file, Long parent_id) throws IOException {
        if (file == null) return;

        String originalFilename = file.getOriginalFilename();
        AccountEntity currentUser = userServices.getCurrentUser();
        String email = currentUser.getEmail();

        Path rootPath = Paths.get(currentUser.getRootPath());

        if (!Files.exists(rootPath)) return;

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = FilenameUtils.getBaseName(file.getOriginalFilename());

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid + "";

        file.transferTo(new File(root + File.separator + uuid));

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(name);
        fileEntity.setUuid(uuidString);
        fileEntity.setOwner(userServices.getCurrentUser());
        fileEntity.setSize(file.getSize());
        fileEntity.setExtension(extension);

        if (parent_id == 0){
            Optional<StorageElement> content = contentRepository.findByName("CONTENT");
            if (!userServices.checkOptionalOnNull(content))return;
            StorageElement rootContent = content.get();
            fileEntity.setParentId(rootContent);
        } else {
            Optional<StorageElement> byId = contentRepository.findById(parent_id);
            if (!userServices.checkOptionalOnNull(byId))return;
            StorageElement storageElement = byId.get();
            fileEntity.setParentId(storageElement);
        }

        fileEntityRepository.saveAndFlush(fileEntity);
    }

    // download file by uuid and response
    @Override
    public ResponseEntity<org.springframework.core.io.Resource> download(String uuid) throws IOException {

        AccountEntity currentUser = userServices.getCurrentUser();
        Long idCurrent = currentUser.getId();

        Optional<FileEntity> byfileUUID = fileEntityRepository.findByUuid(uuid);
        boolean checkedOnNull = userServices.checkOptionalOnNull(byfileUUID);
        if (!checkedOnNull) return null;

        FileEntity fileEntity = byfileUUID.get();


        if (!matchesOwner(idCurrent, fileEntity.getOwner().getId())){
            if (!userServices.adminRightsCheck(currentUser)) return null;
            // user is not admin and not owner of the file
        }

        Path file = Paths.get(root, uuid);

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
    public void createContentPath(String path) {
       Content content = new Content();
       content.setParentId(null);
       content.setName("CONTENT");
       content.setRoot(root);
       contentRepository.saveAndFlush(content);
    }

    // check matches id of the current user and id ot the file owner
    @Override
    public boolean matchesOwner(Long idCurrent, Long ownerId) {
        if (idCurrent == ownerId)return true;
        return false;
    }
}
