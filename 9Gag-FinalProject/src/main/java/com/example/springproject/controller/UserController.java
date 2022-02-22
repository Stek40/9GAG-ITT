package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.*;
import com.example.springproject.dto.newDtos.user.UserCreatedPostsByDate;
import com.example.springproject.dto.newDtos.user.UserRegisterDto;
import com.example.springproject.dto.newDtos.user.UserResponseDtoRegister;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.User;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.UserServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class UserController {

    public static final String LOGGED = "logged";
    public static final String LOGGED_FROM = "loggedFrom";
    public static final String User_Id = "user_id";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    UserServices userServices;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ValidateData data;


    @PostMapping("/users/register")
    public ResponseEntity<UserResponseDtoRegister> register(@RequestBody UserRegisterDto u) {
        return userServices.register(u);
    }
    @GetMapping("users/posts")
    public ResponseEntity<UserCreatedPostsByDate> getUserWithPosts(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.getAllCreatedPosts(request);
    }
    @GetMapping("users")
    public ResponseEntity<UserResponseDtoRegister> getUserById(@RequestParam("id") int id) {
        return userServices.getById(id);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResponseDto> userLogin(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        return userServices.logIn(userLoginDto,request);
    }

    @PostMapping("/users/logout")
    public void userLogout(HttpSession session) {
        session.invalidate();
    }

    public void validateLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute(User_Id) == null ||
                (!(Boolean) session.getAttribute(LOGGED)) ||
                (!request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM)))) {
            throw new UnauthorizedException("You have to login!");
        }
    }

    @PutMapping("/users/edit/profilePicture")
    public ResponseEntity<UserResponseDto> changeProfilePicture(@RequestParam(name = "file")MultipartFile file, HttpServletRequest request) {
       ValidateData.validatorLogin(request);
       User user = userServices.changeProfilePicture(file,request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }

    @PutMapping("/users/edit/changeEmail")
    public ResponseEntity<UserResponseDto> changeEmail(@RequestBody UserEditDto editDto, HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        User u = userServices.changeEmail(editDto, request);
        return ResponseEntity.ok(modelMapper.map(u, UserResponseDto.class));
    }

    @PutMapping("/users/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(@RequestBody UserEditDto editDto, HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        User user = userServices.changePassword(editDto, request);
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }
    @PutMapping("/users/changeUsername")
    public ResponseEntity<UserResponseDto> changeUsername(@RequestBody UserEditDto userEditDto, HttpServletRequest request){
        ValidateData.validatorLogin(request);
        User user = userServices.changeUsername(userEditDto, request);

        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
    @PutMapping("/users/sensitiveContent")
    public ResponseEntity<UserResponseDto> setSensitiveContent(@RequestBody UserEditDto userEditDto,HttpServletRequest request){
        ValidateData.validatorLogin(request);
        User user = userServices.setSensitiveContentTrue(userEditDto, request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
    @PutMapping("/users/isHidden")
    public ResponseEntity<UserResponseDto> setIsHidden(HttpServletRequest request){
        ValidateData.validatorLogin(request);
        User user = userServices.setIsHidden(request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }

    @PutMapping("/users/isPublic")
    public ResponseEntity<UserResponseDto> setIsPublic(HttpServletRequest request){
        ValidateData.validatorLogin(request);
        User user = userServices.setIsPublic(request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
    @PostMapping("/users/delete")
    public ResponseEntity<UserResponseDto> deleteUser(@RequestBody UserEditDto editDto, HttpServletRequest request){
        ValidateData.validatorLogin(request);
        User user = userServices.deleteUser(editDto,request);
        request.getSession().invalidate();
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }

}


