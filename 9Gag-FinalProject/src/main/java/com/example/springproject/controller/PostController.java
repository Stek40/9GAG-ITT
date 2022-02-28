package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.postDtos.DisplayPostDto;
import com.example.springproject.dto.postDtos.PostVoteResultsDto;
import com.example.springproject.dto.postDtos.PostWithoutOwnerDto;
import com.example.springproject.dto.userDtos.UserResponseDto;
import com.example.springproject.dto.userDtos.UserWithAllSavedPostDto;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.PostServices;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.sql.SQLException;
import java.util.*;

@RestController
public class PostController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostServices postServices;
    @Autowired
    private UserController userController;
    @Autowired
    private ModelMapper modelMapper;

    public static final int POSTS_PER_PAGE = 2;


    @SneakyThrows
    @PostMapping("new_post")
    public ResponseEntity<DisplayPostDto> createPost(@RequestParam(name = "file") MultipartFile file,
                                                     @RequestParam(name = "description") String description,
                                                     @RequestParam(name = "categoryId") int categoryId,
                                                     HttpServletRequest request) {
        userController.validateLogin(request);
        long userId = userRepository.getIdByRequest(request);
        DisplayPostDto pDto = postServices.createPost(description, file, userId, categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(pDto);
    }


    @GetMapping("/posts/trending")
    public ResponseEntity<List<PostWithoutOwnerDto>> trendingPosts(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(postServices.getTrendingPosts(page));
    }


    @PutMapping("/posts/save_post")
    public ResponseEntity<UserResponseDto> savePost(@RequestParam("postId") int postId, HttpServletRequest request) {
        userController.validateLogin(request);
        User user = postServices.savedPost(postId, request);
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }

    @PutMapping("/posts/unsave_post")
    public ResponseEntity<UserResponseDto> unSave(@RequestParam("postId") int postId, HttpServletRequest request) {
        userController.validateLogin(request);
        User user = postServices.unSavedPost(postId, request);
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDto.class));
    }

    @Transactional(rollbackFor = SQLException.class)
    @PutMapping("/posts/{id}/upvote")
    public ResponseEntity<PostVoteResultsDto> upvotePost(@PathVariable long id, HttpServletRequest request) {
        userController.validateLogin(request);
        long userId = userRepository.getIdByRequest(request);
        PostVoteResultsDto pDto = postServices.votePost(true, id, userId);
        return ResponseEntity.ok().body(pDto);
    }

    @Transactional(rollbackFor = SQLException.class)
    @PutMapping("/posts/{id}/downvote")
    public ResponseEntity<PostVoteResultsDto> downvotePost(@PathVariable long id, HttpServletRequest request) {
        userController.validateLogin(request);
        long userId = userRepository.getIdByRequest(request);
        PostVoteResultsDto pDto = postServices.votePost(false, id, userId);
        return ResponseEntity.ok().body(pDto);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<DisplayPostDto>> getAllPosts(@RequestParam("sort_by_upvotes") boolean isByUpvotes, @RequestParam("page") int pageNumber) {
        //no login
        List<DisplayPostDto> pDtos = postServices.getAllPosts(isByUpvotes, pageNumber);
        return ResponseEntity.ok().body(pDtos);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<DisplayPostDto> getPostById(@PathVariable long id) {
        //no login
        Post p = postServices.getPostById(id);
        DisplayPostDto pDto = postServices.PostToDisplayPostDtoConversion(p);
        return ResponseEntity.ok().body(pDto);
    }

    @GetMapping("/users/upvoted")
    public ResponseEntity<List<DisplayPostDto>> getUpvotedPosts(HttpServletRequest request) {
        userController.validateLogin(request);
        long userId = userRepository.getIdByRequest(request);
        List<DisplayPostDto> pDtos = postServices.getUpvotedPosts(userId);
        return ResponseEntity.ok().body(pDtos);
    }

    @GetMapping("/post/allSavedPosts")
    public ResponseEntity<UserWithAllSavedPostDto> getAllSavedPost(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        UserWithAllSavedPostDto userWithAllSavedPostDto = postServices.getAllSavedPosts(request);
        return ResponseEntity.ok(userWithAllSavedPostDto);
    }

    @GetMapping("/post/allSavedPostsByVote")
    public ResponseEntity<UserWithAllSavedPostDto> getAllSavedPostByVote(HttpServletRequest request) {
        ValidateData.validatorLogin(request);
        UserWithAllSavedPostDto userWithAllSavedPostDto = postServices.getAllSavedPostsByVote(request);
        return ResponseEntity.ok(userWithAllSavedPostDto);
    }

    @DeleteMapping("/posts/{id}/delete")
    public void deletePost(@PathVariable long id, HttpServletRequest request) {
        userController.validateLogin(request);
        postServices.deletePost(request.getSession(), id);
    }

    @GetMapping("/posts/search/{search}")
    public ResponseEntity<List<DisplayPostDto>> searchPosts(@PathVariable String search) {
        List<DisplayPostDto> result = postServices.searchPostGenerator(search);
        return ResponseEntity.ok().body(result);
    }
}
