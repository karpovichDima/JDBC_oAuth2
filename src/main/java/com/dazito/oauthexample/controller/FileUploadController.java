package com.dazito.oauthexample.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(path = "/uploads")
public class FileUploadController {

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("q") MultipartFile q) {
        String originalFilename = q.getOriginalFilename();
        return originalFilename;
    }
}
