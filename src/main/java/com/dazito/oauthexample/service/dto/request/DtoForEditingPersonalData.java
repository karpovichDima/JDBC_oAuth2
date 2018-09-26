package com.dazito.oauthexample.service.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Random;

@Getter
@Setter
public class DtoForEditingPersonalData implements Serializable{

    private static long serialVersionUID;

    private String newEmail;
    private String newName;
    private String newPassword;
    private String rawOldPassword;

    public DtoForEditingPersonalData(){
        Random random = new Random();
        serialVersionUID = random.nextInt();
    }

}
