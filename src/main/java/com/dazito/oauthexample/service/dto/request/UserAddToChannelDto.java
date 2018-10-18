package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserAddToChannelDto implements Serializable {

    private final static long serialVersionUID = 453422878;

    private Long idUser;
    private Long idChannel;
}
