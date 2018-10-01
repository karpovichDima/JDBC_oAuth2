package com.dazito.oauthexample.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FileService{

    void upload(MultipartFile file) throws IOException;
    void download(String uuid, HttpServletResponse response) throws IOException;
}
