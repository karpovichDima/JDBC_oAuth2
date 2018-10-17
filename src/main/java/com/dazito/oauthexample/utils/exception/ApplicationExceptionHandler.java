package com.dazito.oauthexample.utils.exception;

import com.dazito.oauthexample.service.dto.response.ExceptionDto;
import com.dazito.oauthexample.service.dto.response.GeneralResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity noSuchElementException(AppException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), exception.getResponseCode());
        return buildResponseEntity(error);
    }

    private ResponseEntity<GeneralResponseDto<?>> buildResponseEntity(ExceptionDto exceptionDto) {
        return ResponseEntity.ok(new GeneralResponseDto<>(exceptionDto, null));
    }
}
