package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StorageAddToSomeStructureDto implements Serializable {

    private final static long serialVersionUID = 424894557;

    private Long idStorage;
    private Long idStructure;
}
