package com.example.springproject.services;

import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CountryRepository;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    public User register (String username, String password,
                          String email, String fullName,boolean showSensitiveContent, int countryId, boolean isHidden, char gender){
        if (username.length() < 5 ){
            throw new BadRequestException("The username is short !");
        }
        if (username.isBlank() || username == null){
            throw new BadRequestException("Username is mandatory !");
        }
        if (password.length() < 5){
            throw new BadRequestException("The password is short !");
        }
        if (password.isBlank() || password == null){
            throw new BadRequestException("The password is short !");
        }
        if (email.isBlank() || email == null){
            throw new BadRequestException("Email is mandatory !");
        }
        if (userRepository.findUserByEmailOrUsername(username,email) != null){
            throw new BadRequestException("User already exist !");
        }
        if (countryRepository.getById((long) countryId) == null){
            throw new BadRequestException("Incorrect country !");
        }
        if (gender > 0){

        }
        User u = new User();
        u.set_hidden(isHidden);
        u.setShow_sensitive_content(showSensitiveContent);
        u.setCountry_id(countryId);
        u.setEmail(email);
        u.setFull_name(fullName);

        u.setPassword(password);

    }
}
