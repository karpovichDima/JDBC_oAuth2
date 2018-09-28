package com.dazito.oauthexample.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileService {

    /**
     *  Accepts a file and saves it to root on the server.
     *  @param file is file, which we will save
     */
    void upload(MultipartFile file) throws IOException;

    /**
     *
     */
    // void download(String uuid);


}
