package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Random;

@Getter
@Setter
public class DeleteAccountDto  implements Serializable {

    private static long serialVersionUID;

    private String email;
    private String rawPassword;

    public DeleteAccountDto(){
        Random random = new Random();
        serialVersionUID = random.nextInt();
    }
}
