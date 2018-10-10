package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.ContentService;
import com.dazito.oauthexample.service.FileService;
import com.dazito.oauthexample.service.dto.request.ContentUpdateDto;
import com.dazito.oauthexample.service.dto.response.ContentUpdatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/contents")
public class ContentController {

    @Autowired
    ContentService contentService;

    @Autowired
    FileService fileService;

    @PatchMapping("/")
    @ResponseStatus(value = HttpStatus.OK)
    public ContentUpdatedDto updateContent(@RequestBody ContentUpdateDto contentDto) {
        return contentService.updateContent(contentDto);
    }



}
