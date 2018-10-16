package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathNotExistException extends Exception{

    private String message = "Authorized user is not an administrator.";

    public PathNotExistException() { super(); }
    public PathNotExistException(String message) { super(message); }
    public PathNotExistException(String message, Throwable cause) { super(message, cause); }
    public PathNotExistException(Throwable cause) { super(cause); }
}