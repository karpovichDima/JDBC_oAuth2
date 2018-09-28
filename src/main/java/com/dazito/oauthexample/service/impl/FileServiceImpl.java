package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.config.oauth.UserDetailsConfig;
import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.dao.FileRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.FileEntity;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import static antlr.build.ANTLR.root;

@Service
public class FileServiceImpl implements FileService {


    @Autowired
    FileRepository fileRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public void upload(MultipartFile file) throws IOException {
        if (file == null) return;

        String originalFilename = file.getOriginalFilename();
        AccountEntity currentUser = getCurrentUser();
        Path rootPath = Paths.get(currentUser.getRootPath());

        if (!Files.exists(rootPath))return;

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid + "";

        file.transferTo(new File(root + File.separator + uuid));

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setFileUUID(uuidString);

        fileRepository.saveAndFlush(fileEntity);

    }

    public AccountEntity getCurrentUser() {
        Long id = ((UserDetailsConfig) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser().getId();
        Optional<AccountEntity> optionalById = accountRepository.findById(id);
        if (optionalById.isPresent()) {
            return optionalById.get();
        }
        return null;
    }



    public File createSinglePath(String path){
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

    public File createMultiplyPath(String path){
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
