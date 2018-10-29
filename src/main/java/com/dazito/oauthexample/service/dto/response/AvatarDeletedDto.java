package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AvatarDeletedDto implements Serializable {

    private final static long serialVersionUID = 108423696;

    private Long avatarId;
    private Long avatarOwnerId;
}
