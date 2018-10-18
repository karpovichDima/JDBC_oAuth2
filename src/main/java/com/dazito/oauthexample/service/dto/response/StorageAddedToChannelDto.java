package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StorageAddedToChannelDto implements Serializable {

    private final static long serialVersionUID = 72535899;

    private Long idStorage;
    private Long idChannel;
}
