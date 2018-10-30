package com.dazito.oauthexample.service;

import com.dazito.oauthexample.service.dto.response.DeletedStorageDto;
import com.dazito.oauthexample.utils.exception.AppException;

public interface CollectionService {

    DeletedStorageDto deleteFileFromCollection(Long idCollection, Long idFile) throws AppException;

    DeletedStorageDto deleteCollection(Long idCollection) throws AppException;
}
