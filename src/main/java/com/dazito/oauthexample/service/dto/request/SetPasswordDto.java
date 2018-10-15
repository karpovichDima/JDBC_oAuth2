package com.dazito.oauthexample.service.dto.request;

import com.dazito.oauthexample.model.type.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SetPasswordDto  implements Serializable {

    private final static long serialVersionUID = 752897265;

    private String email;
    private String password;
    private String repeatedPassword;
}