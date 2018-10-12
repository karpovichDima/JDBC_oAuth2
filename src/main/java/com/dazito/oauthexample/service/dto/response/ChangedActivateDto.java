package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChangedActivateDto implements Serializable {

    private final static long serialVersionUID = 84642689;

    private Long id;
    private Boolean isActivated;
}
