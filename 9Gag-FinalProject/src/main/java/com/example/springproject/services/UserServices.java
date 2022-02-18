package com.example.springproject.services;

import com.example.springproject.controller.UserController;
import com.example.springproject.dto.UserEditDto;
import com.example.springproject.dto.UserLoginDto;
import com.example.springproject.dto.UserRegisterDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CountryRepository;
import com.example.springproject.repositories.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    String regexEmail = "^(.+)@(.+)$";

    public User register(UserRegisterDto u) {
        String username = u.getUsername();
        String password = u.getPassword();
        String confirmPassword = u.getConfirmPassword();
        String full_name = u.getFull_name();
        String about = u.getAbout();
        String email = u.getEmail();
        int countryId = (int) u.getCountry_id();
        boolean show_sensitive_content = u.isShow_sensitive_content();
        String gender = u.getGender();
        boolean is_hidden = u.is_hidden();
        String profile_picture_url = u.getProfile_picture_url();
        User user = new User();
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
        validateEmail(email);
        if (userRepository.findUserByUsername(username) != null) {
            throw new BadRequestException("User already exists !");
        }
        if (userRepository.findUserByEmail(email) != null) {
            throw new BadRequestException("User already exists !");
        }
        if (countryRepository.getById((long) countryId) == null) {
            throw new BadRequestException("Invalid country !");
        }
        if (gender != null) {
            user.setGender(gender);
        }
        user.set_hidden(is_hidden);
        user.setShow_sensitive_content(show_sensitive_content);
        user.setCountry_id(countryId);
        user.setEmail(email);
        user.setFull_name(full_name);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        userRepository.save(user);
        return user;
    }

    public User getById(long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new NotFoundException("User not found");
    }

    public User logIn(UserLoginDto userDto) {
        User user = userRepository.findUserByUsername(userDto.getUsername());
        if (user == null) {
            throw new BadRequestException("Incorrect username or password!");
        }
        if (userDto.getUsername() == null || userDto.getUsername().isBlank()) {
            throw new BadRequestException("Username is mandatory !");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new BadRequestException("Password is mandatory !");
        }
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect username or password !");
        }
        return user;
    }

    public User changeProfilePicture(MultipartFile multipartFile, HttpServletRequest request) {
        User user = findUserByRequest(request);

        String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String name = String.valueOf(System.nanoTime())+ "." + ext;
        try {
            Files.copy(multipartFile.getInputStream(), new File("uploads" + File.separator + name).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setProfile_picture_url(name);
        userRepository.save(user);
        return user;
    }

    public User changeEmail(UserEditDto editDto, HttpServletRequest request) {
        User user = findUserByRequest(request);
        if (!passwordEncoder.matches(editDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }
        validateEmail(editDto.getNewEmail());
        if (userRepository.findUserByEmail(editDto.getNewEmail()) != null) {
            throw new BadRequestException("Email already exist !");
        }
        user.setEmail(editDto.getNewEmail());
        userRepository.save(user);
        return user;
    }

    public User changePassword(UserEditDto dto, HttpServletRequest request) {
        User user = findUserByRequest(request);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password !");
        }
        if (!dto.getNewPassword().matches(regexPassword)) {
            throw new BadRequestException("Invalid new password !");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new BadRequestException("The passwords miss match !");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return user;
    }

    public User changeUsername(UserEditDto dto, HttpServletRequest request) {
        User user = findUserByRequest(request);
        if (userRepository.findUserByUsername(dto.getNew_username()) != null) {
            throw new BadRequestException("Username already exist !");
        }
        user.setUsername(dto.getNew_username());
        userRepository.save(user);
        return user;
    }

    public User setSensitiveContentTrue(UserEditDto dto, HttpServletRequest request) {
        User user = findUserByRequest(request);
        user.setShow_sensitive_content(true);
        userRepository.save(user);
        return user;
    }

    public User setIsHidden(HttpServletRequest request) {
        User user = findUserByRequest(request);
        user.set_hidden(true);
        userRepository.save(user);
        return user;
    }

    public User setIsPublic(HttpServletRequest request) {
        User user = findUserByRequest(request);
        user.set_hidden(false);
        userRepository.save(user);
        return user;
    }

    public User deleteUser(UserEditDto editDto, HttpServletRequest request) {
        User user = findUserByRequest(request);
        if (!passwordEncoder.matches(editDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password !");
        }
        userRepository.delete(user);
        return user;
    }

    private boolean validateEmail(String email) {
        if (!email.matches(regexEmail)) {
            throw new BadRequestException("Invalid email !");
        }
        return true;
    }

    private User findUserByRequest(HttpServletRequest request) {
        long id = (long) request.getSession().getAttribute(UserController.User_Id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new NotFoundException("User not found !");
    }
}
