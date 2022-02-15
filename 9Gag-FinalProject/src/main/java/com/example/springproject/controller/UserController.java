package com.example.springproject.controller;

import com.example.springproject.dto.UserLoginDto;
import com.example.springproject.dto.UserRegisterDto;
import com.example.springproject.dto.UserResponseDto;
import com.example.springproject.model.User;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.UserServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
public class UserController {

    public static final String LOGGED = "Logged";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    UserServices userServices;
    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/users/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto u){
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
        User user = userServices.register(username,password,confirmPassword,email,full_name,
                show_sensitive_content,countryId,is_hidden,gender);
        System.out.println(user.getId());
        UserResponseDto userResponseDto = modelMapper.map(user,UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
    }
    @GetMapping("users")
    public ResponseEntity<UserResponseDto> userResponseDtoResponseEntity (@RequestParam("id") int id){
     User user =  userServices.getById(id);
        System.out.println(user.getId());
     UserResponseDto userRespsonse = modelMapper.map(user,UserResponseDto.class);
        return ResponseEntity.ok(userRespsonse);
    }
    @PostMapping("/users/log")
    public ResponseEntity<UserResponseDto> userResponseDtoResponseEntity (@RequestBody UserLoginDto userLoginDto, HttpSession session){
        User user = userServices.logIn(userLoginDto);
        session.setAttribute(LOGGED,true);
        UserResponseDto userResponseDto = modelMapper.map(user,UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
    }

}
