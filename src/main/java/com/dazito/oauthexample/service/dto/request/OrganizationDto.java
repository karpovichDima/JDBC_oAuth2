package com.dazito.oauthexample.service.dto.request;

import com.dazito.oauthexample.model.AccountEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class OrganizationDto implements Serializable {

    private static final long serialVersionUID = 3L;

    private Long id;
    private String salary;
    private String organizationName;
    private List<AccountEntity> users;
}
