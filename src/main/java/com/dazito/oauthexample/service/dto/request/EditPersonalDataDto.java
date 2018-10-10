package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Random;

@Getter
@Setter
public class EditPersonalDataDto implements Serializable{

    private final static long serialVersionUID = 286930429;

    private String newEmail;
    private String newName;
    private String newPassword;
    private String rawOldPassword;

}
