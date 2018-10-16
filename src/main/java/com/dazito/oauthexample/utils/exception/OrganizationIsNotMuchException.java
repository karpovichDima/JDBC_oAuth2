package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationIsNotMuchException extends Exception{

    private String message = "Organization current user and user from account dto is not match.";

    public OrganizationIsNotMuchException() { super(); }
    public OrganizationIsNotMuchException(String message) { super(message); }
    public OrganizationIsNotMuchException(String message, Throwable cause) { super(message, cause); }
    public OrganizationIsNotMuchException(Throwable cause) { super(cause); }
}