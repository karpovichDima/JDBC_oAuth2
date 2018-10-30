package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.CollectionService;
import com.dazito.oauthexample.service.dto.response.DeletedStorageDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/collection")
public class CollectionController extends StructureController{

    @Autowired
    CollectionService collectionService;

    @DeleteMapping("/{idCollection}")
    public ResponseEntity<GeneralResponseDto<DeletedStorageDto>> deleteChannel(@PathVariable Long idCollection) throws AppException {
        DeletedStorageDto deletedStorageDto = collectionService.deleteCollection(idCollection);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, deletedStorageDto));
    }

    @DeleteMapping("/{idCollection}/{idFile}")
    public ResponseEntity<GeneralResponseDto<DeletedStorageDto>> deleteFileFromChannel(@PathVariable Long idCollection, @PathVariable Long idFile) throws AppException {
        DeletedStorageDto deletedStorageDto = collectionService.deleteFileFromCollection(idCollection, idFile);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, deletedStorageDto));
    }

}
