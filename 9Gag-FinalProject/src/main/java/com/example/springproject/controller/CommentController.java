package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.CommentAddDto;
import com.example.springproject.dto.CommentResponseDto;
import com.example.springproject.dto.UserWithCommentsDto;
import com.example.springproject.model.Comment;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CommentRepository;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.CommentServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CommentController {
    @Autowired
    CommentRepository repository;
    @Autowired
    CommentServices commentServices;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;



    @PostMapping("comment/add")
    public ResponseEntity<CommentResponseDto> addComment(@RequestBody CommentAddDto commentRequest, HttpServletRequest request){
        ValidateData.validatorLogin(request);
       Comment comment = commentServices.addComment(commentRequest,request);
       CommentResponseDto commentResponseDto = modelMapper.map(comment,CommentResponseDto.class);
       commentResponseDto.setUserId((Long) request.getSession().getAttribute(UserController.User_Id));

        return ResponseEntity.ok(commentResponseDto);

    }
    @GetMapping("/comment/getAll")
    public ResponseEntity<UserWithCommentsDto> getAllComments(HttpServletRequest request){
        ValidateData.validatorLogin(request);
        User user = userRepository.getById((Long) request.getSession().getAttribute(UserController.User_Id));
//        UserWithCommentsDto userWithCommentsDto = new UserWithCommentsDto();
//        Set<Comment> comments = user.getComments();
//        userWithCommentsDto.setComments(comments.stream().map(comment -> modelMapper.map(comment,CommentWithoutOwnerDto.class)).collect(Collectors.toSet()));

        return ResponseEntity.ok(modelMapper.map(user,UserWithCommentsDto.class));
    }
//    @GetMapping("comment/getAllPosts")
//    public ResponseEntity


}
