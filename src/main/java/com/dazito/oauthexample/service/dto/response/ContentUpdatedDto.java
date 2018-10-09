package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ContentUpdatedDto implements Serializable {

    private final static long serialVersionUID = 421502874;

    private Long id;
    private String newRoot;
    private String newName;
}
