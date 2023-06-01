package com.group7.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.group7.project.mailserver.Email;
import com.group7.project.mailserver.EmailSenderService;

@Service
public class MailController {

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/sendSimpleMail")
    public void sendMail(Email email) {

        try {
            emailSenderService.sendSimpleMail(email.getToEmail(), email.getFromEmail(), email.getSubject(),
                    email.getBody());
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }
}
