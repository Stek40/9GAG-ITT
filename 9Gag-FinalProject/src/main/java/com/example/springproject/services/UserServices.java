package com.example.springproject.services;

import com.example.springproject.dto.UserEditDto;
import com.example.springproject.dto.UserLoginDto;
import com.example.springproject.dto.UserResponseDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CountryRepository;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).*[A-Za-z0-9].{6,}$";
    public User register(String username, String password, String confirmPassword,
                         String email, String fullName, boolean showSensitiveContent, int countryId, boolean isHidden,
                         String gender) {

        String regexEmail = "^(.+)@(.+)$";
        User u = new User();
        if (!password.matches(regexPassword)) {
            throw new BadRequestException("Invalid password");
        }
        if (username.length() < 5) {
            throw new BadRequestException("The username is short !");
        }
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("The password miss match !");
        }
        if (username.isBlank() || username == null) {
            throw new BadRequestException("Username is mandatory !");
        }

        if (email.isBlank() || email == null) {
            throw new BadRequestException("Email is mandatory !");
        }
        if (!email.matches(regexEmail)) {
            throw new BadRequestException("Invalid email !");
        }
        if (userRepository.findUserByUsername(username) != null) {
            throw new BadRequestException("User already exists !");
        }
        if (userRepository.findUserByEmail(email) != null) {
            throw new BadRequestException("User already exists !");
        }
        if (countryRepository.getById((long) countryId) == null) {
            throw new BadRequestException("Incorrect country !");
        }
        if (gender != null) {
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
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new NotFoundException("User not found");
    }

    public User logIn(UserLoginDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isBlank()) {
            throw new BadRequestException("Username is mandatory !");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new BadRequestException("Password is mandatory !");
        }
        User user = userRepository.findUserByUsername(userDto.getUsername());
        if (user == null) {
            throw new BadRequestException("Incorrect username or password!");
        }

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect username or password !");
        }


        return user;
    }

    public User changeProfilePicture(long id, String url) {
        Optional<User> userOpt = userRepository.findById(id);
        String regexUrl = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if (!url.matches(regexUrl)) {
            throw new BadRequestException("Invalid url address !");
        }
        if (userOpt.isPresent()) {
            userOpt.get().setProfile_picture_url(url);
            userRepository.save(userOpt.get());
            return userOpt.get();

        }
        throw new NotFoundException("User not found");

    }

    public User changeEmail(UserEditDto editDto) {
        Optional<User> user = userRepository.findById(editDto.getId());
        if (user.isPresent()) {
            if (userRepository.findUserByEmail(editDto.getNewEmail()) != null) {
                throw new BadRequestException("Email already exist !");
            }
            user.get().setEmail(editDto.getNewEmail());
            userRepository.save(user.get());
            return user.get();
        }
        throw new NotFoundException("User not found");
    }

    public User changePassword(long id, String password, String newPassword, String confirmNewPassword) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
            if (!passwordEncoder.matches(password,user.get().getPassword())){
                throw new BadRequestException("Invalid password !");
            }
            if (!newPassword.matches(regexPassword)){
                throw new BadRequestException("Invalid new password !");
            }
            if (!newPassword.equals(confirmNewPassword)){
                throw new BadRequestException("The passwords miss match !");
            }
            user.get().setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user.get());
            return user.get();
        }
        throw new NotFoundException("User not found !");
    }
}
