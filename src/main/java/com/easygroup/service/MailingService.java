package com.easygroup.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;
import org.springframework.stereotype.Service;

@Service
public class MailingService {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean auth;
    private final boolean starttls;
    
    public MailingService(String host, int port, String username, String password, 
                          boolean auth, boolean starttls) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.starttls = starttls;
    }
    
    // Simple constructor with default settings
    public MailingService(String host, String username, String password) {
        this(host, 587, username, password, true, true);
    }
    
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
    
    /**
     * Sends a simple text email
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
     * Sends an HTML email
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
     * Sends an email with attachments
     */
    public void sendEmailWithAttachment(String from, String to, String subject, 
                                       String body, File[] attachments) 
            throws MessagingException {
        Session session = createSession();
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        
        // Create the message body part
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);
        
        // Create a multipart message
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        
        // Add attachments
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
        
        // Set the complete message parts
        message.setContent(multipart);
        
        // Send the message
        Transport.send(message);
    }
}