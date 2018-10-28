package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChannelCreatedDto implements Serializable{

    private final static long serialVersionUID = 257932767;
    private String channelName;
    private Long id;
}
