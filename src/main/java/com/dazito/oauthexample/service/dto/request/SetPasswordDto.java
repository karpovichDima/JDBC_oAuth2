package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SetPasswordDto  implements Serializable {

    private final static long serialVersionUID = 752897265;

    private String email;
    private String uuid;
    private String password;
}