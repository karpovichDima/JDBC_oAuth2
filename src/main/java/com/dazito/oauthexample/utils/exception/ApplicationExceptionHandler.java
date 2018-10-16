package com.dazito.oauthexample.utils.exception;

import com.dazito.oauthexample.service.dto.response.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.xml.bind.ValidationException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String NO_MESSAGE_PROVIDED = "No message provided. Please contact to administrator.";
    private static final String SERVER_ERROR = "Server error";

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity noSuchElementException(NoSuchElementException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.NOT_FOUND);
        return buildResponseEntity(error);
    }
//
//    @ExceptionHandler(CreationSignupServiceException.class)
//    public ResponseEntity handleDataValidation(CreationSignupServiceException exception) {
//        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, SERVER_ERROR, exception);
//        error.setDebugMessage(NO_MESSAGE_PROVIDED);
//        return buildResponseEntity(error);
//    }
//
//    @ExceptionHandler(ActivationTokenException.class)
//    public ResponseEntity handleActivationTokenException(ActivationTokenException exception) {
//        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
//        return buildResponseEntity(error);
//    }
//
//    @ExceptionHandler(Throwable.class)
//    public ResponseEntity<?> handleGlobalError(Throwable exception) {
//        ApiError error = new ApiError(HttpStatus.CONFLICT, exception);
//        return buildResponseEntity(error);
//    }


    private ResponseEntity<?> buildResponseEntity(ExceptionDto exceptionDto) {
        return new ResponseEntity<>(exceptionDto, exceptionDto.getHttpStatus());
    }

}
