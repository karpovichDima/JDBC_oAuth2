package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentUserIsNotAdminException extends Exception{

    private String message = "Authorized user is not an administrator.";

    public CurrentUserIsNotAdminException() { super(); }
    public CurrentUserIsNotAdminException(String message) { super(message); }
    public CurrentUserIsNotAdminException(String message, Throwable cause) { super(message, cause); }
    public CurrentUserIsNotAdminException(Throwable cause) { super(cause); }
}