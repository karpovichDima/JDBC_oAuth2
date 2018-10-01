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
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {


    @Autowired
    FileEntityRepository fileEntityRepository;

    @Resource(name = "userService")
    UserService userServices;

    @Value("${root.path}")
    String root;

    @Override
    public void upload(MultipartFile file) throws IOException {
        if (file == null) return;

        String originalFilename = file.getOriginalFilename();
        AccountEntity currentUser = userServices.getCurrentUser();
        Path rootPath = Paths.get(currentUser.getRootPath());

        if (!Files.exists(rootPath)) return;

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid + "";

        file.transferTo(new File(root + File.separator + uuid));

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setFileUUID(uuidString);

        fileEntityRepository.saveAndFlush(fileEntity);
    }

    @Override
    public void download(String uuid, HttpServletResponse response) throws IOException {
        Path file = Paths.get(root, uuid);
        if (Files.exists(file)) {
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
                //byte[] data = Files.readAllBytes(file);
            } catch (Exception e){}
        }
    }


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
