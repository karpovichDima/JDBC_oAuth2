package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NameDto {

    @NotNull
    private String name;
}
