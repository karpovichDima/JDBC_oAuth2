package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.dto.request.DirectoryDto;
import com.dazito.oauthexample.service.dto.response.DirectoryCreated;
import com.dazito.oauthexample.service.dto.response.FileUploadResponse;
import com.dazito.oauthexample.service.dto.response.StorageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;


@RestController
@RequestMapping(path = "/files")
public class FileController {

    @Value("${root.path}")
    String root;

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }


    @PostMapping("/upload/{parentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public FileUploadResponse upload(@RequestParam MultipartFile file, @PathVariable Long parentId) throws IOException {
        return fileService.upload(file, parentId);
    }

    @GetMapping("/download/{uuid:.+}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Resource> download(@PathVariable String uuid) throws IOException {
        return fileService.download(uuid);
    }

    @PostMapping("/dir")
    @ResponseStatus(value = HttpStatus.OK)
    public DirectoryCreated createDirectory(@RequestBody DirectoryDto directoryDto) {
        return fileService.createDirectory(directoryDto);
    }

    @GetMapping("/chierarchy/{id:.+}")
    @ResponseStatus(value = HttpStatus.OK)
    public StorageDto createHierarchy(@PathVariable Long id) throws IOException {
        return fileService.buildStorageDto(id);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("2000MB");
        factory.setMaxRequestSize("2000MB");
        return factory.createMultipartConfig();
    }
}
