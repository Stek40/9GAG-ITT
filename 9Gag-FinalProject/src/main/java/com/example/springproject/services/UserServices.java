package com.example.springproject.services;

import com.example.springproject.dto.UserLoginDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CountryRepository;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register (String username, String password,String confirmPassword,
                          String email, String fullName,boolean showSensitiveContent, int countryId, boolean isHidden,
                          String gender){
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).*[A-Za-z0-9].{6,}$";
        User u = new User();
        if (!password.matches(regex)){
            throw new BadRequestException("Incorrect password");
        }
        if (username.length() < 5 ){
            throw new BadRequestException("The username is short !");
        }
        if (!password.equals(confirmPassword)){
            throw new BadRequestException("The password miss match !");
        }
        if (username.isBlank() || username == null){
            throw new BadRequestException("Username is mandatory !");
        }

        if (email.isBlank() || email == null){
            throw new BadRequestException("Email is mandatory !");
        }
        if (userRepository.findUserByUsername(username) != null){
            throw new BadRequestException("User already exists !");
        }
        if (userRepository.findUserByEmail(email)!= null){
            throw new BadRequestException("User already exists !");
        }
        if (countryRepository.getById((long) countryId) == null){
            throw new BadRequestException("Incorrect country !");
        }
        if (gender!=null){
            u.setGender(gender);
        }
        u.set_hidden(isHidden);
        u.setShow_sensitive_content(showSensitiveContent);
        u.setCountry_id(countryId);
        u.setEmail(email);
        u.setFull_name(fullName);
        u.setPassword(passwordEncoder.encode(password));
        u.setUsername(username);
        userRepository.save(u);
        return u;
    }

    public User getById(long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()){
            return opt.get();
        }
        throw new NotFoundException("User not found");
    }
    public User logIn(UserLoginDto userDto){
        User user = userRepository.findUserByUsername(userDto.getUsername());
        if (user == null){
            throw new NotFoundException("Incorrect username!");
        }
        return user;

    }
}
