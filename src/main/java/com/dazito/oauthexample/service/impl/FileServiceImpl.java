package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.FileEntityRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {


    @Autowired
    FileEntityRepository fileEntityRepository;

    @Resource(name = "userService")
    UserService userServices;

    @Value("${root.path}")
    String root;

    // upload multipart file on the server
    @Override
    public void upload(MultipartFile file) throws IOException {
        if (file == null) return;

        String originalFilename = file.getOriginalFilename();
        AccountEntity currentUser = userServices.getCurrentUser();
        String email = currentUser.getEmail();

        Path rootPath = Paths.get(currentUser.getRootPath());

        if (!Files.exists(rootPath)) return;


        UUID uuid = UUID.randomUUID();
        String uuidString = uuid + "";

        file.transferTo(new File(root + File.separator + uuid));

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setFileUUID(uuidString);
        fileEntity.setOwner(email);

        fileEntityRepository.saveAndFlush(fileEntity);
    }

    // download file by uuid and response
    @Override
    public void download(String uuid, HttpServletResponse response) throws IOException {

        AccountEntity currentUser = userServices.getCurrentUser();
        String emailCurrent = currentUser.getEmail();

        Optional<FileEntity> byfileUUID = fileEntityRepository.findByfileUUID(uuid);
        boolean checkedOnNull = userServices.checkOptionalOnNull(byfileUUID);
        if (!checkedOnNull) return;

        FileEntity fileEntity = byfileUUID.get();
        String ownerEmail = fileEntity.getOwner();

        if (!matchesOwner(emailCurrent, ownerEmail)){
            if (!userServices.adminRightsCheck(currentUser)) return;
            // user is not admin and not owner of the file
        }

        Path file = Paths.get(root, uuid);
        if (Files.exists(file)) {
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
                //byte[] data = Files.readAllBytes(file);
            } catch (Exception e){}
        }
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

    // check matches email of the current user and email ot the file owner
    @Override
    public boolean matchesOwner(String emailCurrent, String ownerEmail) {
        if (emailCurrent.equals(ownerEmail))return true;
        return false;
    }
}
