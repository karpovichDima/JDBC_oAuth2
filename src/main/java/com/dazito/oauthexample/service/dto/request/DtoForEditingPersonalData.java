package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class DtoForEditingPersonalData implements Serializable{

    private static final long serialVersionUID = 2L;

    private String newEmail;
    private String newName;
    private String newPassword;
    private String rawOldPassword;


}
