package com.example.springproject.controller;

import com.example.springproject.model.User;
import com.example.springproject.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public User register(@RequestBody User u){
        userRepository.save(u);
        return u;
    }
}
