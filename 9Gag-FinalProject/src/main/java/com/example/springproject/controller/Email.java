package com.example.springproject.controller;

import com.example.springproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class Email {

    @Autowired
    private JavaMailSender javaMailSender;

    public void SendEmailVerification(String email, String token, long userId) {
        String url = "http://localhost:9999/users/verified?id=" + userId + "&?token=" + token;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("martin9gag@gmail.com");
        simpleMailMessage.setText(url);
        simpleMailMessage.setSubject("9Gag verify email !");
        simpleMailMessage.setTo(email);
        javaMailSender.send(simpleMailMessage);
    }

    public void SendEmailChangePassword(String email, String token, long userId) {
        String url = "Follow the link to change your password in 9Gag." +
                " http://localhost:9999/users/newPassword?id=" + userId + "&?token=" + token;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("9Gag");
        simpleMailMessage.setSubject("9gag change password !");
        simpleMailMessage.setText(url);
        simpleMailMessage.setTo(email);
        javaMailSender.send(simpleMailMessage);
    }

    public void sendMessageHappyBirthday(User u) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("9Gag");
        simpleMailMessage.setSubject("9gag Happy Birthday !");
        simpleMailMessage.setText("Happy birthday !");
        simpleMailMessage.setTo(u.getEmail());
        javaMailSender.send(simpleMailMessage);
    }
}
