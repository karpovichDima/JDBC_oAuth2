package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class UserDto {

    @NotNull
    private String username;
    @NotNull
    private String password;
}
