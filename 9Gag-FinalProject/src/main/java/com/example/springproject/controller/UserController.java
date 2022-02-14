package com.example.springproject.controller;

import com.example.springproject.model.User;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users/register")
    public User register(@RequestBody User u){
        String username = u.getUsername();
        String password = u.getPassword();
        String full_name = u.getFull_name();
        String about = u.getAbout();
        String email = u.getEmail();
        boolean show_sensitive_content = u.isShow_sensitive_content();
        char gender = u.getGender();
        boolean is_hidden = u.is_hidden();
        String profile_picture_url = u.getProfile_picture_url();
        userRepository.save(u);
        return u;
    }
}
