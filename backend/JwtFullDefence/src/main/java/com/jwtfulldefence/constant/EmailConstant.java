package com.jwtfulldefence.constant;

public class EmailConstant {

    public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    public static final String SIMPLE_EMAIL_TRANSFER_PROTOCOL_SECURE = "smtps";
    public static final String USERNAME = System.getenv("EMAIL_USERNAME");
    public static final String PASSWORD = System.getenv("EMAIL_PASSWORD");
    public static final String FROM_EMAIL = "applicationtestingnz@gmail.com";
    public static final String CC_EMAIL = "";
    public static final String EMAIL_SUBJECT = "Application Testing - New Password";

    public static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String SMTP_AUTH = "mail.smtp.auth";
    public static final String SMTP_PORT = "mail.smtp.port";
    public static final int DEFAULT_PORT = 465;
    public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
    public static final String MAIL_SMTP_SSL_ENABLED = "mail.smtp.ssl.enable";


}
