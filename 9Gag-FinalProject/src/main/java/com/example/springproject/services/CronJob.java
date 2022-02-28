package com.example.springproject.services;

import com.example.springproject.controller.Email;
import com.example.springproject.model.User;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CronJob {


    @Autowired
    private Email email;
    @Autowired
    private UserRepository userRepository;


    @Scheduled(cron = "0 0 12 * * *")
    public void sendEmailsToUsersBirthday() {
        List<User> birthday = userRepository.findUserBirthday();
        for (User u : birthday) {
            email.sendMessageHappyBirthday(u);
        }
    }

}
