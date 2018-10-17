package com.dazito.oauthexample.service.dto.response;

import com.dazito.oauthexample.model.type.ResponseCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
public class ExceptionDto implements Serializable {

    private final static long serialVersionUID = 472144877;

    private String message;
    private ResponseCode responseCode;

    public ExceptionDto(String message, ResponseCode responseCode) {
        this.message = message;
        this.responseCode = responseCode;
    }
}
