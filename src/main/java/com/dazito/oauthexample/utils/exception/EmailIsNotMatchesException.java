package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailIsNotMatchesException extends Exception{

    private String message = "Authorized user is not an administrator.";

    public EmailIsNotMatchesException() { super(); }
    public EmailIsNotMatchesException(String message) { super(message); }
    public EmailIsNotMatchesException(String message, Throwable cause) { super(message, cause); }
    public EmailIsNotMatchesException(Throwable cause) { super(cause); }
}