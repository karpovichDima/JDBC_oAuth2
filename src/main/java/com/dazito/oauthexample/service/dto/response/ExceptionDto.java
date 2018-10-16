package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
public class ExceptionDto implements Serializable {

    private final static long serialVersionUID = 472144877;

    private String message;
    private HttpStatus httpStatus;

    public ExceptionDto(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
