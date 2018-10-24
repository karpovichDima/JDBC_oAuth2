package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UpdateStorageOnChannel implements Serializable {

    private final static long serialVersionUID = 463577777;

    private Long idEditStorage;
    private Long idCurrentParent;
    private Long idNewParent;
    private Long idChannel;
}
