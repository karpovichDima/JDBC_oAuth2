package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.IOException;


@RestController
@RequestMapping(path = "/files")
public class FileUploadController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file) throws IOException {
        fileService.upload(file);
    }


    @PostMapping("/test")
    public String handleFileUpload(@RequestParam MultipartFile file) throws IOException {
        if (file == null) return null;
        String originalFilename = file.getOriginalFilename();
        return originalFilename;
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



        @Bean
        public MultipartConfigElement multipartConfigElement () {
            MultipartConfigFactory factory = new MultipartConfigFactory();
            factory.setMaxFileSize("2000MB");
            factory.setMaxRequestSize("2000MB");
            return factory.createMultipartConfig();
        }
    }
