package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.commentDtos.AllCommentsOnPostDto;
import com.example.springproject.dto.commentDtos.CommentResponseDto;
import com.example.springproject.dto.postDtos.DisplayPostDto;
import com.example.springproject.dto.userDtos.UserWithCommentsDto;
import com.example.springproject.services.CommentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
public class CommentController {

    @Autowired
    CommentServices commentServices;


    @PutMapping("comment/add")
    public ResponseEntity<CommentResponseDto> addComment(@RequestParam(name = "file") MultipartFile file,
                                                         @RequestParam(name = "text") String text,
                                                         @RequestParam(name = "postId") long postId,
                                                         HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return commentServices.createComment(file, text, postId, request);

    }

    @PutMapping("/comment/upvote")
    public ResponseEntity<CommentResponseDto> upvote(@RequestParam(name = "commentId") long commentId,
                                                     HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return commentServices.upVoteComment(commentId, request);
    }

    @PutMapping("/comment/downvote")
    public ResponseEntity<CommentResponseDto> downVote(@RequestParam(name = "commentId") long commentId,
                                                       HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return commentServices.downVoteComment(commentId, request);
    }

    @PutMapping("/comment/removeVote")
    public ResponseEntity<CommentResponseDto> removeVot(@RequestParam(name = "commentId") long commentId,
                                                        HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return commentServices.removeVote(commentId, request);
    }

    @GetMapping("/comment/getAll")
    public ResponseEntity<UserWithCommentsDto> getAllCommentsByUpVote(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return commentServices.getAllCommentsUser(request);
    }

    @GetMapping("comment/getAllPosts")
    public ResponseEntity<Set<DisplayPostDto>> getAllCommentPosts(HttpServletRequest request) {
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

    @DeleteMapping("/comment/remove")
    public ResponseEntity<CommentResponseDto> removeComment(@RequestParam(name = "commentId") long postId, HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        return commentServices.removeComment(postId, request);
    }
}
