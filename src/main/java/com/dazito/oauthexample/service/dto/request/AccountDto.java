package com.dazito.oauthexample.service.dto.request;

import com.dazito.oauthexample.model.type.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Random;

@Setter
@Getter
public class AccountDto implements Serializable {

    private static long serialVersionUID;

    private Long id;
    private UserRole role;
    private String email;
    private String username;
    private String password;
    private Boolean isActivated;
    private String organizationName;

    public AccountDto(){
        Random random = new Random();
        serialVersionUID = random.nextInt();
    }
}
