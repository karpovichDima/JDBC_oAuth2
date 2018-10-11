package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FileUpdateDto implements Serializable {

    private final static long serialVersionUID = 453456456;

    private String newName;
    private Long newParentId;
    private String uuid;
}
