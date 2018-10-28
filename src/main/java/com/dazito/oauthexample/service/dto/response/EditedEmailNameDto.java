package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EditedEmailNameDto implements Serializable{

    private final static long serialVersionUID = 484661590;
    private String email;
    private String username;
    private String uuid;
    private Long contentId;
}
