package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.utils.Mail;

import javax.xml.bind.ValidationException;

public interface MailService {

    /**
     * send email to the specified address
     * @param mail this is the address to which we send the letter
    */
    void sendEmail(Mail mail);

    /**
     * preparation and filling of a new user creation letter and password
     * @param accountEntity the entity from which we get the information to fill the letter
     */
    void emailPreparation(AccountEntity accountEntity) throws ValidationException;

    /**
     * preparing a password recovery letter
     * @param email the address to which the password will be restored
     */
    void emailPreparation(String email) throws ValidationException;
}



