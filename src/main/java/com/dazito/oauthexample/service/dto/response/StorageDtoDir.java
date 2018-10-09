package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class StorageDtoDir extends StorageDto implements Serializable {

    private final static long serialVersionUID = 567567568;

    List<StorageDto> childrenFiles;
    List<StorageDto> childrenDirectories;
}
