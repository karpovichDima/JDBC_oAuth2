package com.dazito.oauthexample.utils.exception;

import com.dazito.oauthexample.model.type.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AppException extends Exception {

    private String message;
    private ResponseCode responseCode;
}
