package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DeletedUserDto implements Serializable {

    private final static long serialVersionUID = 34563447;

    private String message;
}
