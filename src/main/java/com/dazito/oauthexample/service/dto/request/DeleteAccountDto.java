package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DeleteAccountDto  implements Serializable {

    private static final long serialVersionUID = 7L;

    private String email;
    private String rawPassword;
}
