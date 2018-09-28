package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Random;

@Getter
@Setter
public class EmailNameDto implements Serializable{

    private final static long serialVersionUID = 484661590;
    private String email;
    private String username;

}
