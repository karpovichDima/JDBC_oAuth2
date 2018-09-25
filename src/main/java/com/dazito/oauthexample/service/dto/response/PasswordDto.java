package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class PasswordDto implements Serializable {

    private static final long serialVersionUID = 6L;
    @NotNull
    private String password;

}
