package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StorageUpdateDto implements Serializable {

    private final static long serialVersionUID = 456775758;

    private Long id;
    private String newName;
    private Long newParentId;
    private String uuid;
    private String newRoot;
}
