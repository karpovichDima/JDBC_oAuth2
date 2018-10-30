package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.MailService;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.utils.Mail;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("mailService")
public class MailServiceImpl implements MailService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    VelocityEngine velocityEngine;

    @Autowired
    UserService userService;

    @Value("${email.admin}")
    String emailAdmin;

    public void emailPreparation(AccountEntity accountEntity) throws ValidationException {

        Mail mail = new Mail();
        mail.setMailFrom(emailAdmin);
        mail.setMailTo(accountEntity.getEmail());
        mail.setMailSubject("Set a password for your account");

        String reference = "referenceToFormForSetPassword/" + accountEntity.getUuid();

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("firstName", accountEntity.getUsername());
        model.put("lastName", "");
        model.put("location", "Belarus");
        model.put("signature", "");
        model.put("reference", reference);
        mail.setModel(model);

        sendEmail(mail);
    }

    @Override
    public void emailPreparation(String email) throws ValidationException {
        AccountEntity foundUser = userService.getCurrentUser();
        String uuid = UUID.randomUUID() + "";
        foundUser.setUuid(uuid);
        userService.saveAccount(foundUser);

        String username = foundUser.getUsername();

        Mail mail = new Mail();
        mail.setMailFrom(emailAdmin);
        mail.setMailTo(email);
        mail.setMailSubject("Forgot password");

        String reference = "referenceToFormForSetPassword/" + uuid;

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("firstName", username);
        model.put("lastName", "");
        model.put("location", "Belarus");
        model.put("signature", "");
        model.put("reference", reference);
        mail.setModel(model);

        sendEmail(mail);
    }

    public void sendEmail(Mail mail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject(mail.getMailSubject());
            mimeMessageHelper.setFrom(mail.getMailFrom());
            mimeMessageHelper.setTo(mail.getMailTo());
            mail.setMailContent(geContentFromTemplate(mail.getModel()));
            mimeMessageHelper.setText(mail.getMailContent(), true);

            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String geContentFromTemplate(Map<String, Object> model) {
        StringBuffer content = new StringBuffer();
        try {
            content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/templates/email-template.vm", model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }


}
