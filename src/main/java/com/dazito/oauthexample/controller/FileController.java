package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.dto.request.FileUpdateDto;
import com.dazito.oauthexample.service.dto.response.FileDeletedDto;
import com.dazito.oauthexample.service.dto.response.FileUploadedDto;
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


    @PostMapping("/{parentId}")
    public ResponseEntity<FileUploadedDto> upload(@RequestParam MultipartFile file, @PathVariable Long parentId) throws IOException {
        FileUploadedDto upload = fileService.upload(file, parentId);
        return ResponseEntity.ok(upload);
    }

    @GetMapping("/{uuid:.+}")
    public ResponseEntity<Resource> download(@PathVariable String uuid) throws IOException {
        return fileService.download(uuid);
    }

    @PostMapping("/update/{uuid:.+}")
    public ResponseEntity<FileUploadedDto> update(@RequestParam MultipartFile file, @PathVariable String uuid) throws IOException {
        FileUploadedDto fileUploadedDto = fileService.updateFile(file, uuid);
        return ResponseEntity.ok(fileUploadedDto);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FileDeletedDto> delete(@PathVariable String uuid) throws IOException {
        FileDeletedDto deleted = fileService.delete(uuid);
        return ResponseEntity.ok(deleted);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("2000MB");
        factory.setMaxRequestSize("2000MB");
        return factory.createMultipartConfig();
    }
}
