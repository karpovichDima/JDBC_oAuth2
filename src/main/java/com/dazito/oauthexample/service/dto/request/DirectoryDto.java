package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DirectoryDto implements Serializable {

    private final static long serialVersionUID = 395263742;

    private Long id;
    private Long parentId;
    private String name;
}
