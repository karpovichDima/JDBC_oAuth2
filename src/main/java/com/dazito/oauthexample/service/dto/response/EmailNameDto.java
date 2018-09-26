package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Random;

@Getter
@Setter
public class EmailNameDto implements Serializable{

    private static long serialVersionUID;

    private String email;
    private String username;

    public EmailNameDto(){
        Random random = new Random();
        serialVersionUID = random.nextInt();
    }
}
