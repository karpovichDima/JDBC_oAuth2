package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class PasswordDto implements Serializable {

    @NotNull
    private String password;

}
