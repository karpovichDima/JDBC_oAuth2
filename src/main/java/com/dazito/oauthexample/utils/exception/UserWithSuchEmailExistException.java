package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithSuchEmailExistException extends Exception{

    private String message = "User with such email exist.";

    public UserWithSuchEmailExistException() { super(); }
    public UserWithSuchEmailExistException(String message) { super(message); }
    public UserWithSuchEmailExistException(String message, Throwable cause) { super(message, cause); }
    public UserWithSuchEmailExistException(Throwable cause) { super(cause); }
}