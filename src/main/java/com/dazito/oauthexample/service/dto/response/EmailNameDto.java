package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EmailNameDto implements Serializable{

    private static final long serialVersionUID = 4L;

    private String email;
    private String username;
}
