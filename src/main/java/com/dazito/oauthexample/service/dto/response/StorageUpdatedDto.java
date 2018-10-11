package com.dazito.oauthexample.service.dto.response;

import java.io.Serializable;

public class StorageUpdatedDto implements Serializable {

    private final static long serialVersionUID = 56875867;

    private Long id;
    private String newName;
    private Long newParentId;
    private String uuid;
    private String newRoot;
}
