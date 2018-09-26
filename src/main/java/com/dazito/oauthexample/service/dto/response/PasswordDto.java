package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Random;

@Setter
@Getter
public class PasswordDto implements Serializable {

    private static long serialVersionUID;
    @NotNull
    private String password;

    public PasswordDto(){
        Random random = new Random();
        serialVersionUID = random.nextInt();
    }
}
