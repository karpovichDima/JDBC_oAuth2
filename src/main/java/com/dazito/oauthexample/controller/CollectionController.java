package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.CollectionService;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/collection")
public class CollectionController extends StructureController{

    @Autowired
    CollectionService collectionService;





}
