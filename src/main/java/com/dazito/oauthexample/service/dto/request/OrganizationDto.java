package com.dazito.oauthexample.service.dto.request;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Organization;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

@Setter
@Getter
public class OrganizationDto implements Serializable {

    private final static long serialVersionUID = 759674978;

    private Long id;
    private String salary;
    private String organizationName;
    private List<AccountEntity> users;

}
