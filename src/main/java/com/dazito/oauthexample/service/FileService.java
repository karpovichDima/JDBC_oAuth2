package com.dazito.oauthexample.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public interface FileService{

    /**
     * upload multipart file
     * @param file which we want to upload on the server
     */
    void upload(MultipartFile file) throws IOException;

    /**
     * upload multipart file
     * @param uuid is uuid of the file which we want to download on the client
     */
    void download(String uuid, HttpServletResponse response) throws IOException;

    /**
     * check matches email of the current user and email ot the file owner
     * @param emailCurrent is email of the current user
     * @param ownerEmail is email ot the file owner
     * @return true = if emailCurrent == ownerEmail
     */
    boolean matchesOwner(String emailCurrent, String ownerEmail);

    /**
     * create single directory by path
     * @param path of the which we will create directory
     * @return new File directory
     */
    File createSinglePath(String path);

    /**
     * create multiply path directory by path
     * @param path of the which we will create directories
     * @return new File directory
     */
    File createMultiplyPath(String path);
}
