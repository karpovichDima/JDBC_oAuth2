package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Random;

@Setter
@Getter
public class EditedPasswordDto implements Serializable {

    private final static long serialVersionUID = 653407663;
    @NotNull
    private String password;

}
