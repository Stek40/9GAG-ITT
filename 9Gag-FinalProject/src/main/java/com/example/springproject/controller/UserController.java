package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.userDtos.*;
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
    UserServices userServices;


    @GetMapping("/users/verified")
    public ResponseEntity<String> verifiedProfile(@RequestParam(name = "id") long id,
                                                  @RequestParam(name = "?token") String token) {

        return userServices.verifyUser(id, token);
    }

    @PostMapping("/users/newPassword")
    public ResponseEntity<String> setNewPassword(@RequestParam(name = "id") long id, @RequestParam(name = "?token") String token,
                                                 @RequestBody UserEditDto userEditDto, HttpServletRequest request) {
        return userServices.setNewPassword(id, token, userEditDto, request);
    }

    @PostMapping("/users/forgottenPassword")
    public ResponseEntity<String> sendEmail(@RequestBody UserEditDto userEditDto) {

        return userServices.sendEmailPassword(userEditDto);
    }

    @PostMapping("/users/addFavouriteCategories")
    public ResponseEntity<UserResponseDto> addFavouriteCategories(@RequestParam(name = "categoryId") long cId,
                                                                  HttpServletRequest request) {
        validateLogin(request);
        return userServices.addCategory(cId, request);
    }

    @PostMapping("/users/removeFavouriteCategories")
    public ResponseEntity<UserResponseDto> removeFavouriteCategories(@RequestParam(name = "categoryId")
                                                                             long cId, HttpServletRequest request) {
        validateLogin(request);
        return userServices.removeCategory(cId, request);
    }

    @PostMapping("/users/register")
    public ResponseEntity<UserResponseDtoRegister> register(@RequestBody UserRegisterDto u) {
        return userServices.register(u);
    }

    @GetMapping("users/posts/byDate")
    public ResponseEntity<UserCreatedPostsByDate> getUserWithPosts(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.getAllCreatedPosts(request);
    }

    @GetMapping("users/posts/byVote")
    public ResponseEntity<UserCreatedPostsByDate> getUserWithPostsByVote(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.getAllCreatedPostsByVote(request);
    }

    @GetMapping("users/{userId}/posts/")
    public ResponseEntity<UserCreatedPostsByDate> getUserByIdWithAllPosts(@RequestParam(name = "userId") long userId) {
        return userServices.getUserByIdWithAllPosts(userId);
    }


    @GetMapping("users")
    public ResponseEntity<UserResponseDto> getUserById(@RequestParam("id") int id) {
        return userServices.getById(id);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResponseDto> userLogin(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        return userServices.logIn(userLoginDto, request);
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
    public ResponseEntity<UserResponseDto> changeProfilePicture(@RequestParam(name = "file") MultipartFile file,
                                                                HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.changeProfilePicture(file, request);
    }

    @PutMapping("/users/edit/changeEmail")
    public ResponseEntity<UserResponseDto> changeEmail(@RequestBody UserEditDto editDto,
                                                       HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.changeEmail(editDto, request);
    }

    @PutMapping("/users/changePassword")
    public ResponseEntity<UserResponseDto> changePassword(@RequestBody UserEditDto editDto, HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.changePassword(editDto, request);
    }

    @PutMapping("/users/changeUsername")
    public ResponseEntity<UserResponseDto> changeUsername(@RequestBody UserEditDto userEditDto, HttpServletRequest request) {
        ValidateData.validatorLogin(request);

        return userServices.changeUsername(userEditDto, request);
    }

    @PutMapping("/users/sensitiveContent")
    public ResponseEntity<UserResponseDto> setSensitiveContent(HttpServletRequest request) {
        ValidateData.validatorLogin(request);

        return userServices.setSensitiveContentTrue(request);
    }

    @PutMapping("/users/isHidden")
    public ResponseEntity<UserResponseDto> setIsHidden(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.setIsHidden(request);
    }

    @PutMapping("/users/isPublic")
    public ResponseEntity<UserResponseDto> setIsPublic(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.setIsPublic(request);
    }

    @DeleteMapping("/users/delete")
    public ResponseEntity<String> deleteUser(@RequestBody UserEditDto editDto, HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return userServices.deleteUser(editDto, request);
    }

}


