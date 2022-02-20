package com.example.springproject.controller;

import com.example.springproject.dto.*;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.UserServices;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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


    @PostMapping("/users/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto u) {
        User user = userServices.register(u);
        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
    }
    @GetMapping("users/posts")
    public ResponseEntity<UserWithPostsDto> getUserWithPosts(@RequestParam("id") int id) {
//        User user = userServices.getById(id);
        return ResponseEntity.ok(modelMapper.map(userRepository.findById((long) id).get(), UserWithPostsDto.class));
       // Set<Post> posts = user.getPosts();
//        System.out.println(posts.size() + " -------------------------");
//        Set<PostWithoutOwnerDto> withoutOwnerDtos = new HashSet<>();
//        for (Post post:posts) {
//            withoutOwnerDtos.add(modelMapper.map(post,PostWithoutOwnerDto.class));
//        }

//        UserWithPostsDto userRespsonse = modelMapper.map(user, UserWithPostsDto.class);
//        System.out.println(userRespsonse.getPosts().size() + " ---------------------");
//        //userRespsonse.getPosts().addAll(withoutOwnerDtos);
//        return ResponseEntity.ok(userRespsonse);
    }

    @GetMapping("users")
    public ResponseEntity<UserResponseDto> getUserById(@RequestParam("id") int id) {
        User user = userServices.getById(id);
        System.out.println(user.getId());
        UserResponseDto userRespsonse = modelMapper.map(user, UserResponseDto.class);
        return ResponseEntity.ok(userRespsonse);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResponseDto> userLogin(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        User user = userServices.logIn(userLoginDto);
        HttpSession session = request.getSession();
        session.setAttribute(LOGGED, true);
        session.setAttribute(LOGGED_FROM, request.getRemoteAddr());
        session.setAttribute(User_Id, user.getId());
        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
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
        validateLogin(request);
       User user = userServices.changeProfilePicture(file,request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }

    @PutMapping("/users/edit/changeEmail")
    public ResponseEntity<UserResponseDto> changeEmail(@RequestBody UserEditDto editDto, HttpServletRequest request) {
        validateLogin(request);
        User u = userServices.changeEmail(editDto, request);
        return ResponseEntity.ok(modelMapper.map(u, UserResponseDto.class));
    }

    @PutMapping("/users/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(@RequestBody UserEditDto editDto, HttpServletRequest request) {
        validateLogin(request);
        User user = userServices.changePassword(editDto, request);
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }
    @PutMapping("/users/changeUsername")
    public ResponseEntity<UserResponseDto> changeUsername(@RequestBody UserEditDto userEditDto, HttpServletRequest request){
        validateLogin(request);
        User user = userServices.changeUsername(userEditDto, request);

        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
    @PutMapping("/users/sensitiveContent")
    public ResponseEntity<UserResponseDto> setSensitiveContent(@RequestBody UserEditDto userEditDto,HttpServletRequest request){
        validateLogin(request);
        User user = userServices.setSensitiveContentTrue(userEditDto, request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
    @PutMapping("/users/isHidden")
    public ResponseEntity<UserResponseDto> setIsHidden(HttpServletRequest request){
        validateLogin(request);
        User user = userServices.setIsHidden(request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }

    @PutMapping("/users/isPublic")
    public ResponseEntity<UserResponseDto> setIsPublic(HttpServletRequest request){
        validateLogin(request);
        User user = userServices.setIsPublic(request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
    @PostMapping("/users/delete")
    public ResponseEntity<UserResponseDto> deleteUser(@RequestBody UserEditDto editDto, HttpServletRequest request){
        validateLogin(request);
        User user = userServices.deleteUser(editDto,request);
        request.getSession().invalidate();
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }




}


