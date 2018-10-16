package com.dazito.oauthexample.utils.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationIsNotMuch extends Exception{

    public OrganizationIsNotMuch() { super(); }
    public OrganizationIsNotMuch(String message) { super(message); }
    public OrganizationIsNotMuch(String message, Throwable cause) { super(message, cause); }
    public OrganizationIsNotMuch(Throwable cause) { super(cause); }
}