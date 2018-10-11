package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DirectoryDeletedDto implements Serializable {

    private final static long serialVersionUID = 48742482;

    private Long id;
    private Long parentId;
    private String Name;
}