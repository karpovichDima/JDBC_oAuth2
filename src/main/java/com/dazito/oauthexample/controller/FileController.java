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
import java.io.IOException;


@RestController
@RequestMapping(path = "/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Value("${root.path}")
    String root;

    @PostMapping("/upload/{parent_id}")
    public void upload(@RequestParam MultipartFile file, @PathVariable Long parent_id) throws IOException {
        fileService.upload(file, parent_id);
    }

    @GetMapping("/download/{uuid:.+}")
    public ResponseEntity<Resource> download(@PathVariable String uuid) throws IOException {
        return fileService.download(uuid);
    }

    @PostMapping("/root/{path}")
    public void root(@PathVariable String path) {
        fileService.createContentPath(path);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("2000MB");
        factory.setMaxRequestSize("2000MB");
        return factory.createMultipartConfig();
    }
}
