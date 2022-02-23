package com.example.springproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Email {

    @Autowired
    private JavaMailSender javaMailSender;

    public void SendEmail(String email,String token, long userId){

        String url = "http://localhost:9999/users/verified?id="+userId+"&?token="+token;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("martin9gag@gmail.com");
        simpleMailMessage.setText(url);
        simpleMailMessage.setTo(email);
        javaMailSender.send(simpleMailMessage);
    }
}
