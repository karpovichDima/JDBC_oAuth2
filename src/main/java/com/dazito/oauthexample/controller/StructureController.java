package com.dazito.oauthexample.controller;

import com.dazito.oauthexample.service.StructureService;
import com.dazito.oauthexample.service.dto.request.CreateSomeStructureDto;
import com.dazito.oauthexample.service.dto.request.StorageAddToSomeStructureDto;
import com.dazito.oauthexample.service.dto.response.DeletedStorageDto;
import com.dazito.oauthexample.service.dto.response.SomeStructureCreatedDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import com.dazito.oauthexample.service.dto.response.StorageAddedToSomeStructureDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/structure")
public class StructureController {

    @Autowired
    StructureService structureService;

    @PostMapping("/add/file")
    public ResponseEntity<GeneralResponseDto<StorageAddedToSomeStructureDto>> addStorageElementToSomeStructure(@RequestBody StorageAddToSomeStructureDto storageAddToSomeStructureDto) throws AppException {
        StorageAddedToSomeStructureDto response = structureService.addStorageToSomeStructure(storageAddToSomeStructureDto);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, response));
    }

    @PostMapping("/")
    public ResponseEntity<GeneralResponseDto<SomeStructureCreatedDto>> createSomeStructure(@RequestBody CreateSomeStructureDto createSomeStructure) throws AppException {
        SomeStructureCreatedDto structure = structureService.createSomeStructure(createSomeStructure);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, structure));
    }

    @DeleteMapping("/{idStructure}/{idStorage}")
    public ResponseEntity<GeneralResponseDto<DeletedStorageDto>> deleteStorageFromStructure(@PathVariable Long idStructure, @PathVariable Long idStorage) throws AppException {
        DeletedStorageDto deletedStorageDto = structureService.deleteStorageFromStructure(idStructure, idStorage);
        return ResponseEntity.ok(new GeneralResponseDto<>(null, deletedStorageDto));
    }




}
