package com.example.springproject.controller;

import com.example.springproject.dto.*;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.User;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.UserServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class UserController {

    public static final String LOGGED = "Logged";
    public static final String LOGGED_FROM = "loggedFrom";
    public static final String User_Id = null;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    UserServices userServices;
    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/users/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto u) {
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
        User user = userServices.register(username, password, confirmPassword, email, full_name,
                show_sensitive_content, countryId, is_hidden, gender);
        System.out.println(user.getId());
        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("users")
    public ResponseEntity<UserResponseDto> getUserById(@RequestParam("id") int id) {
        User user = userServices.getById(id);
        System.out.println(user.getId());
        UserResponseDto userRespsonse = modelMapper.map(user, UserResponseDto.class);
        return ResponseEntity.ok(userRespsonse);
    }

    @PostMapping("/users/log")
    public ResponseEntity<UserResponseDto> userLogin(@RequestBody UserLoginDto userLoginDto, HttpSession session, HttpServletRequest request) {
        User user = userServices.logIn(userLoginDto);
        session.setAttribute(LOGGED, true);
        session.setAttribute(LOGGED_FROM, request.getRemoteAddr());
        //session.setAttribute(User_Id, user.getId());
        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/users/logout")
    public void userLogout(HttpSession session) {
        session.invalidate();
    }

    private void validateLogin(HttpSession session, HttpServletRequest request) {
        if (session.isNew() ||
                (!(Boolean) session.getAttribute(LOGGED)) ||
                (!request.getRemoteAddr().equals(session.getAttribute(LOGGED_FROM)))) {
            throw new UnauthorizedException("You have to login!");
        }
    }

    @PutMapping("/users/edit/profilePicture")
    public ResponseEntity<UserChangeProfilePictureDto> changeProfilePicture(@RequestBody UserChangeProfilePictureDto dto, HttpSession session, HttpServletRequest request) {
        validateLogin(session, request);
        User u = userServices.changeProfilePicture(dto.getId(), dto.getProfile_picture_url());

        UserChangeProfilePictureDto userChangeProfilePictureDto = modelMapper.map(u, UserChangeProfilePictureDto.class);
        return ResponseEntity.ok(userChangeProfilePictureDto);
    }

    @PutMapping("/users/edit/changeEmail")
    public ResponseEntity<UserResponseDto> changeEmail(@RequestBody UserEditDto editDto, HttpSession session, HttpServletRequest request) {
        validateLogin(session, request);
        User u = userServices.changeEmail(editDto);
        return ResponseEntity.ok(modelMapper.map(u, UserResponseDto.class));
    }

    @PutMapping("/users/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(@RequestBody UserEditDto editDto, HttpSession session, HttpServletRequest request) {
        validateLogin(session, request);
        User user = userServices.changePassword(editDto.getId(), editDto.getPassword(), editDto.getNewPassword(), editDto.getConfirmNewPassword());
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }
}


