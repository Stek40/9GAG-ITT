package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.*;
import com.example.springproject.model.Comment;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CommentRepository;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.CommentServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping("comment/add")
    public ResponseEntity<CommentResponseDto> addComment(@RequestParam(name = "file") MultipartFile file,
                                                         @RequestParam(name = "text")String text,@RequestParam(name = "postId") long postId,
                                                         HttpServletRequest request){
        ValidateData.validatorLogin(request);
        long userId = userRepository.getIdByRequest(request);
          Comment comment = commentServices.createComment(file, text, postId, userId);
         CommentResponseDto commentResponseDto = modelMapper.map(comment,CommentResponseDto.class);
         commentResponseDto.setUserId(userId);
        return ResponseEntity.ok(commentResponseDto);

    }
    @PutMapping("/comment/upvote")
    public ResponseEntity<CommentResponseDto> upvote(@RequestParam (name = "commentId") long commentId, HttpServletRequest request){
        ValidateData.validatorLogin(request);
        Comment comment = commentServices.upVoteComment(commentId, request);
        User user = userRepository.getUserByRequest(request);
        return ResponseEntity.ok(modelMapper.map(comment,CommentResponseDto.class));
    }
    @PutMapping("/comment/downvote")
    public ResponseEntity<UserResponseDto> downVote(@RequestParam (name = "commentId") long commentId, HttpServletRequest request){
        ValidateData.validatorLogin(request);
        Comment comment = commentServices.dowVoteComment(commentId, request);
        User user = userRepository.getUserByRequest(request);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
//    @PutMapping("/comment/removeVote")
//    public ResponseEntity<UserResponseDto> removeVote(@RequestParam (name = "commentId") long commentId, HttpServletRequest request){
//        ValidateData.validatorLogin(request);
//        Comment comment = commentServices.removeVote(commentId, request);
//        User user = userRepository.getUserByRequest(request);
//        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
//    }

//    @PostMapping("comment/add")
//    public ResponseEntity<CommentResponseDto> addComment(@RequestBody CommentAddDto commentRequest, HttpServletRequest request){
//        ValidateData.validatorLogin(request);
//       Comment comment = commentServices.addComment(commentRequest,request);
//       CommentResponseDto commentResponseDto = modelMapper.map(comment,CommentResponseDto.class);
//       commentResponseDto.setUserId((Long) request.getSession().getAttribute(UserController.User_Id));
//
//        return ResponseEntity.ok(commentResponseDto);
//
//    }
    @GetMapping("/comment/getAll")
    public ResponseEntity<UserWithCommentsDto> getAllComments(HttpServletRequest request){
        ValidateData.validatorLogin(request);
        User user = userRepository.getUserByRequest(request);
//        UserWithCommentsDto userWithCommentsDto = new UserWithCommentsDto();
//        Set<Comment> comments = user.getComments();
//        userWithCommentsDto.setComments(comments.stream().map(comment -> modelMapper.map(comment,CommentWithoutOwnerDto.class)).collect(Collectors.toSet()));

        return ResponseEntity.ok(modelMapper.map(user,UserWithCommentsDto.class));
    }
    @GetMapping("comment/getAllPosts")
    public ResponseEntity<UserWithAllPost> getAllCommentPosts(HttpServletRequest request){
        ValidateData.validatorLogin(request);
        User user = userRepository.getUserByRequest(request);
        System.out.println(user.getComments().size());
        return ResponseEntity.ok(modelMapper.map(user, UserWithAllPost.class));
    }


}
