package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping(path = "/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Value("${root.path}")
    String root;

    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file) throws IOException {
        fileService.upload(file);
    }

    @GetMapping("/download/{uuid:.+}")
    public ResponseEntity<Resource> download(@PathVariable String uuid) throws IOException {
        return fileService.download(uuid);
    }


    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("2000MB");
        factory.setMaxRequestSize("2000MB");
        return factory.createMultipartConfig();
    }
}
