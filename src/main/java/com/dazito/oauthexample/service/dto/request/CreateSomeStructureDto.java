package com.dazito.oauthexample.service.dto.request;

import com.dazito.oauthexample.model.type.SomeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSomeStructureDto {

    private final static long serialVersionUID = 247427897;

    private Long idStorage;
    private String structureName;
    private SomeType typeCreatedStructure;
}
