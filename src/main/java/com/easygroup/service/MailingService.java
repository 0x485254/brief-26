package com.easygroup.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class MailingService {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean auth;
    private final boolean starttls;

    /**
     * Constructeur complet pour personnaliser entièrement la config SMTP.
     */
    public MailingService(String host, int port, String username, String password,
            boolean auth, boolean starttls) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.starttls = starttls;
    }

    /**
     * Constructeur par défaut avec port 587, authentification et STARTTLS activés.
     */
    public MailingService(String host, String username, String password) {
        this(host, 587, username, password, true, true);
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.starttls.enable", String.valueOf(starttls));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * Envoie un e-mail texte simple.
     */
    public void sendTextEmail(String from, String to, String subject, String body)
            throws MessagingException {
        Session session = createSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    /**
     * Envoie un e-mail HTML.
     */
    public void sendHtmlEmail(String from, String to, String subject, String htmlBody)
            throws MessagingException {
        Session session = createSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(htmlBody, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    /**
     * Envoie un e-mail avec des pièces jointes.
     */
    public void sendEmailWithAttachment(String from, String to, String subject,
            String body, File[] attachments)
            throws MessagingException {
        Session session = createSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        if (attachments != null) {
            for (File file : attachments) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                try {
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);
                } catch (Exception e) {
                    throw new MessagingException("Failed to attach file: " + file.getName(), e);
                }
            }
        }

        message.setContent(multipart);

        Transport.send(message);
    }
}
