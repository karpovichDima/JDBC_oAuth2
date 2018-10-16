package com.dazito.oauthexample.utils.exception;

import com.dazito.oauthexample.service.dto.response.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity noSuchElementException(NoSuchElementException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.NOT_FOUND);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(CurrentUserIsNotAdminException.class)
    public ResponseEntity userNotAdmin(CurrentUserIsNotAdminException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.FORBIDDEN);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(UserWithSuchEmailExistException.class)
    public ResponseEntity userAlreadyExist(UserWithSuchEmailExistException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.CONFLICT);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(EmptyFieldException.class)
    public ResponseEntity emptyField(EmptyFieldException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.NOT_FOUND);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(PasswordNotMatchesException.class)
    public ResponseEntity passwordsNotMatches(PasswordNotMatchesException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.CONFLICT);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(OrganizationIsNotMuchException.class)
    public ResponseEntity organizationsNotMatches(OrganizationIsNotMuchException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.CONFLICT);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity typeMatches(TypeMismatchException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.CONFLICT);
        return buildResponseEntity(error);
    }

    @ExceptionHandler(PathNotExistException.class)
    public ResponseEntity pathNotExist(PathNotExistException exception) {
        ExceptionDto error = new ExceptionDto(exception.getMessage(), HttpStatus.NOT_FOUND);
        return buildResponseEntity(error);
    }

    private ResponseEntity<?> buildResponseEntity(ExceptionDto exceptionDto) {
        return new ResponseEntity<>(exceptionDto, exceptionDto.getHttpStatus());
    }

}
