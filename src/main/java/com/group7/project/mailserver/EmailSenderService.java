package com.group7.project.mailserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;


    public void sendSimpleMail(String toEmail, String fromEmail, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(fromEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message); 
    }

    public String sendMailWithAttachment(String toEmail, String subject, String body) {
        return null;
    }
}
