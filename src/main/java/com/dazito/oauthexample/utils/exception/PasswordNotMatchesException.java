package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordNotMatchesException extends Exception{

    private String message = "Passwords not matches.";

    public PasswordNotMatchesException() { super(); }
    public PasswordNotMatchesException(String message) { super(message); }
    public PasswordNotMatchesException(String message, Throwable cause) { super(message, cause); }
    public PasswordNotMatchesException(Throwable cause) { super(cause); }
}