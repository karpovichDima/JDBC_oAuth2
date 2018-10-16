package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmptyFieldException extends Exception{

    private String message = "Field is empty";

    public EmptyFieldException() { super(); }
    public EmptyFieldException(String message) { super(message); }
    public EmptyFieldException(String message, Throwable cause) { super(message, cause); }
    public EmptyFieldException(Throwable cause) { super(cause); }
}