package com.jwtfulldefence.service;


import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.jwtfulldefence.constant.EmailConstant.*;

@Service
public class EmailService {


    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {

        Message message = createEmail(firstName, password, email);

        Transport.send(message);
    }

    /*TODO: ADD HTML EMAIL TEMPLATE*/
    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        StringBuilder text = new StringBuilder();

        message.setText(new StringBuilder()
                .append("Hello ").append(firstName)
                .append(", \n \n")
                .append("Your new account password is ").append(password)
                .append("\n \n")
                .append("Company Name").toString());
        message.setSentDate(new Date());
        message.saveChanges();

        return message;

    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(MAIL_TRANSPORT_PROTOCOL, SIMPLE_EMAIL_TRANSFER_PROTOCOL_SECURE);
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, "true");
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, "true");
        properties.put(SMTP_STARTTLS_REQUIRED, "true");
        properties.put(MAIL_SMTP_SSL_ENABLED, "true");
//        properties.put("mail.debug", "true");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });


    }

}
