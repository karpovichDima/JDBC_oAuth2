package com.dazito.oauthexample.service.dto.request;

import com.dazito.oauthexample.model.Organization;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class AccountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String role;
    private String email;
    private String username;
    private String password;
    private Boolean isActivated;
    private String organizationName;
}
