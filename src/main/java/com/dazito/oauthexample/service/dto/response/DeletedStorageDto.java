package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DeletedStorageDto implements Serializable {

    private final static long serialVersionUID = 247587854;

    private Long idDeletedStorage;
    private String nameDeletedStorage;
}
