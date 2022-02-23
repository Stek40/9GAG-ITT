package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.*;
import com.example.springproject.dto.newDtos.comment.AllCommentsOnPostDto;
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
import java.util.*;
import java.util.stream.Collectors;

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
                                                         @RequestParam(name = "text") String text, @RequestParam(name = "postId") long postId,
                                                         HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        long userId = userRepository.getIdByRequest(request);
        Comment comment = commentServices.createComment(file, text, postId, userId);

        CommentResponseDto commentResponseDto = modelMapper.map(comment, CommentResponseDto.class);
        commentResponseDto.setUserId(userId);
        return ResponseEntity.ok(commentResponseDto);

    }

    @PutMapping("/comment/upvote")
    public ResponseEntity<CommentResponseDto> upvote(@RequestParam(name = "commentId") long commentId, HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        Comment comment = commentServices.upVoteComment(commentId, request);
        return ResponseEntity.ok(modelMapper.map(comment, CommentResponseDto.class));
    }

    @PutMapping("/comment/downvote")
    public ResponseEntity<CommentResponseDto> downVote(@RequestParam(name = "commentId") long commentId, HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        Comment comment = commentServices.dowVoteComment(commentId, request);
        return ResponseEntity.ok(modelMapper.map(comment, CommentResponseDto.class));
    }

    @PutMapping("/comment/removeVote")
    public ResponseEntity<CommentWithoutOwnerDto> removeVot(@RequestParam(name = "commentId") long commentId, HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        Comment comment = commentServices.removeVot(commentId, request);
        return ResponseEntity.ok(modelMapper.map(comment, CommentWithoutOwnerDto.class));
    }

    @GetMapping("/comment/getAll")
    public ResponseEntity<UserWithCommentsDto> getAllCommentsByUpVote(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        User user = userRepository.getUserByRequest(request);

        return ResponseEntity.ok(modelMapper.map(user, UserWithCommentsDto.class));
    }

    @GetMapping("comment/getAllPosts")
    public ResponseEntity<Set<PostWithoutCommentPostDto>> getAllCommentPosts(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return ResponseEntity.ok(commentServices.getAllCommentPosts(request));
    }

    @GetMapping("/allCommentByVote/post")
    public AllCommentsOnPostDto getAllCommentsByUpVote(@RequestParam(name = "id") long postId) {
        return commentServices.getAllCommentByPostId(postId);
    }

    @GetMapping("/allCommentByDate/post")
    public AllCommentsOnPostDto getAllCommentsByDate(@RequestParam(name = "id") long postId) {
        return commentServices.getAllCommentByPostDate(postId);
    }
}
