package com.FlightReservation.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    public static void sendEmail(String recipientEmail, String subject, String message) {
        // Sender's email address and password
        String senderEmail = "praveshmajalikar6@gmail.com";
        String senderPassword = "rmdakcjqetknrzjn";

        // Gmail SMTP server settings
        String smtpHost = "smtp.gmail.com";
        int smtpPort = 587;

        // Email content type
        String contentType = "text/plain";

        // Create JavaMail session properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a new message
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(senderEmail));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            emailMessage.setSubject(subject);
            emailMessage.setContent(message, contentType);

            // Send the message using Transport class
            Transport.send(emailMessage);

            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            System.out.println("Failed to send email. Error: " + e.getMessage());
        }
    }
}


