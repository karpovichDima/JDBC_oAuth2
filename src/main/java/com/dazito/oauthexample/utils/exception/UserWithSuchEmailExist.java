package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithSuchEmailExist extends Exception{

    public UserWithSuchEmailExist() { super(); }
    public UserWithSuchEmailExist(String message) { super(message); }
    public UserWithSuchEmailExist(String message, Throwable cause) { super(message, cause); }
    public UserWithSuchEmailExist(Throwable cause) { super(cause); }
}