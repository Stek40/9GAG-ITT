package com.example.springproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Email {

    @Autowired
    private JavaMailSender javaMailSender;

    public void SendEmailVerification(String email, String token, long userId){
        String url = "http://localhost:9999/users/verified?id="+userId+"&?token="+token;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("martin9gag@gmail.com");
        simpleMailMessage.setText(url);
        simpleMailMessage.setTo(email);
        javaMailSender.send(simpleMailMessage);
    }
    public void SendEmailChangePassword(String email, String token, long userId){
        String url = "Follow the link to change your password in 9Gag." +
                " http://localhost:9999/users/newPassword?id="+userId+"&?token="+token;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("9Gag");
        simpleMailMessage.setText(url);
        simpleMailMessage.setTo(email);
        javaMailSender.send(simpleMailMessage);
    }
}
