package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class FileDeletedDto implements Serializable {

    private final static long serialVersionUID = 782154654;

    private Long id;
    private Long parentId;
    private String Name;
    private String uuid;
}