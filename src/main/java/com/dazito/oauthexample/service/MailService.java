package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.Mail;
import com.dazito.oauthexample.service.dto.request.SetPasswordDto;

import javax.xml.bind.ValidationException;

public interface MailService {

    void sendEmail(Mail mail);

    void emailPreparation(AccountEntity accountEntity) throws ValidationException;

}



