package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GetAllStorageElementDto implements Serializable {

    private final static long serialVersionUID = 425869973;

    private Long idChannel;
}
