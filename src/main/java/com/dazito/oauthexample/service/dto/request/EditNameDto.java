package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class EditNameDto implements Serializable {

    @NotNull
    private String newName;
}
