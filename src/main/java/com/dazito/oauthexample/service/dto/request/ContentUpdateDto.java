package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ContentUpdateDto implements Serializable {

    private final static long serialVersionUID = 424802874;

    private Long id;
    private String newRoot;
    private String newName;
}
