package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dto {

    Status status;

    public Dto() {
        this.status = Status.FAIL;
    }

    public Dto(Status status) {
        this.status = status;
    }

    public enum Status{
        SUCCESS,FAIL
    }
}
