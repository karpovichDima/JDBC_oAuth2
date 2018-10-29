package com.dazito.oauthexample.service.dto.response;

import com.dazito.oauthexample.model.type.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AvatarCreatedDto implements Serializable {

    private final static long serialVersionUID = 108423696;

    private Long avatarId;
    private Long avatarOwnerId;
}
