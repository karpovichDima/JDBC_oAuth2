package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.ContentService;
import com.dazito.oauthexample.service.dto.request.ContentUpdateDto;
import com.dazito.oauthexample.service.dto.response.ContentUpdatedDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/contents")
public class ContentController {

    private final ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PatchMapping("/")
    public ResponseEntity<GeneralResponseDto<ContentUpdatedDto>> updateContent(@RequestBody ContentUpdateDto contentDto) throws AppException {
        ContentUpdatedDto contentUpdatedDto = contentService.updateContent(contentDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, contentUpdatedDto));
    }

}
