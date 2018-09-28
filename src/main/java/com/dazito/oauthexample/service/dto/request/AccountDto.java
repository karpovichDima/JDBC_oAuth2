package com.dazito.oauthexample.service.dto.request;

import com.dazito.oauthexample.model.type.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Random;

@Setter
@Getter
public class AccountDto implements Serializable {

    private final static long serialVersionUID = 108423696;

    private Long id;
    private UserRole role;
    private String email;
    private String username;
    private String password;
    private Boolean isActivated;
    private String organizationName;

}
