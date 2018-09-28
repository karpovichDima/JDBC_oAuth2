package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Random;

@Getter
@Setter
public class DeleteAccountDto  implements Serializable {

    private final static long serialVersionUID = 421502874;

    private String email;
    private String rawPassword;
}
