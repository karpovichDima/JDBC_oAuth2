package com.dazito.oauthexample.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class GeneralResponseDto<T> implements Serializable {

    private final static long serialVersionUID = 236152554;

    private ExceptionDto error;
    private T data;
}
