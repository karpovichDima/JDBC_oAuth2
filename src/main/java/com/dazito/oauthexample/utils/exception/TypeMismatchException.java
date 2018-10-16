package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeMismatchException extends Exception{

    private String message = "A different type of object was expected.";

    public TypeMismatchException() { super(); }
    public TypeMismatchException(String message) { super(message); }
    public TypeMismatchException(String message, Throwable cause) { super(message, cause); }
    public TypeMismatchException(Throwable cause) { super(cause); }
}