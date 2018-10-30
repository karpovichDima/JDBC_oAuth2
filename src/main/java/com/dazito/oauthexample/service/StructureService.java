package com.dazito.oauthexample.service;

import com.dazito.oauthexample.service.dto.request.CreateSomeStructureDto;
import com.dazito.oauthexample.service.dto.request.StorageAddToSomeStructureDto;
import com.dazito.oauthexample.service.dto.response.DeletedStorageDto;
import com.dazito.oauthexample.service.dto.response.SomeStructureCreatedDto;
import com.dazito.oauthexample.service.dto.response.StorageAddedToSomeStructureDto;
import com.dazito.oauthexample.utils.exception.AppException;

public interface StructureService {

    StorageAddedToSomeStructureDto addStorageToSomeStructure(StorageAddToSomeStructureDto storageAddToSomeStructureDto) throws AppException;

    SomeStructureCreatedDto createSomeStructure(CreateSomeStructureDto dto) throws AppException;
}
