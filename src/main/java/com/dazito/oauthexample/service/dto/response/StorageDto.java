package com.dazito.oauthexample.service.dto.response;

import com.dazito.oauthexample.model.type.SomeType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class StorageDto implements Serializable {

    private final static long serialVersionUID = 34534526;

    Long id;
    String name;
    SomeType type;
    long size;
}
