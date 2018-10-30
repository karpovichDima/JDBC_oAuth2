package com.dazito.oauthexample.service.dto.response;

import com.dazito.oauthexample.model.type.SomeType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SomeStructureCreatedDto implements Serializable{

    private final static long serialVersionUID = 257932767;
    private String channelName;
    private SomeType createdStructureType;
    private Long id;
}
