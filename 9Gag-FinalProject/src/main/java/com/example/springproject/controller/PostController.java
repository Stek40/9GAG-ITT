package com.example.springproject.controller;

import com.example.springproject.dto.*;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.Post;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.PostServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostServices postServices;
    @Autowired
    private UserController userController;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/new_post")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<PostDto> createPost(@RequestBody Post p, HttpSession session, HttpServletRequest request) {
        userController.validateLogin(request);
        long user_id = (Long)session.getAttribute(UserController.User_Id);
        p.setOwner(userRepository.findById(user_id).get());
        p = postServices.create(p);
        postRepository.save(p);
        PostDto dto = modelMapper.map(p, PostDto.class);
        dto.setUserId(user_id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PutMapping("/save_post")
    public void savePost(@RequestBody Post p, HttpServletRequest request) {
        userController.validateLogin(request);
        if(false) {//check in Users_saved_posts if already saved

        }
    }

    @PutMapping(value = {"/posts/{id}/upvote", "/posts/{id}/downvote"})
    public ResponseEntity<PostDto> votePost(@PathVariable long id, HttpServletRequest request) {
        userController.validateLogin(request);
        Post p = postRepository.getById(id);
        if(request.getRequestURI().contains("up")) {
            this.vote(p, true);
        }else {
            this.vote(p, false);
        }
        postRepository.save(p);
        //resp.addIntHeader("Upvotes", p.getUpvotes());
        //resp.addIntHeader("Downvotes", p.getDownvotes());
        PostDto dto = modelMapper.map(p, PostDto.class);
        return ResponseEntity.ok(dto);
    }
    private void vote(Post p, boolean isUpvote) {
        if(isUpvote) {
            if(false) {//already upvoted
                //delete entry from users_upvote_posts
                p.setUpvotes(p.getUpvotes() - 1);
            }
            else if(false) {//already downvoted
                //remove entry from users_downvote_posts
                p.setUpvotes(p.getUpvotes() + 1);
                p.setDownvotes(p.getDownvotes() - 1);
            }
            else {
                p.setUpvotes(p.getUpvotes() + 1);
            }
        }
        else {
            if(false) {//already downvoted
                //delete entry from users_downvote_posts
                p.setDownvotes(p.getDownvotes() - 1);
            }
            else if(false) {//already upvoted
                //remove entry from users_upvote_posts
                p.setDownvotes(p.getDownvotes() + 1);
                p.setUpvotes(p.getUpvotes() - 1);
            }
            else {
                p.setDownvotes(p.getDownvotes() + 1);
            }
        }
    }
    @GetMapping("/all_posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        //no need to be logged
        return ResponseEntity.ok(postRepository.findAll());
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostWithOwnerAndCategoryDto> getPostById(@PathVariable long id) {
        //no need to be logged
        Post p = postRepository.findById(id).orElseThrow(() -> new NotFoundException("post with id=" + id + " doesn't exist"));
        PostWithOwnerAndCategoryDto pDto = modelMapper.map(p, PostWithOwnerAndCategoryDto.class);
        pDto.setUserId(p.getOwner().getId());
        pDto.setOwner(modelMapper.map(p.getOwner(), UserWithoutPostsDto.class));
        pDto.setCategory(modelMapper.map(p.getCategory(), CategoryWithoutPostsDto.class));
        return ResponseEntity.ok().body(pDto);
        //return ResponseEntity.ok(postRepository.findById(id).orElseThrow(() -> new NotFoundException("post with id=" + id + " doesn't exist")));
    }
    @GetMapping("/posts/{id}/download")
    public String downloadPostMedia(@PathVariable long id, HttpServletRequest request) {
        userController.validateLogin(request);
        if(!postRepository.existsById(id)) {
            throw new NotFoundException("post with id=" + id + " doesn't exist");
        }
        return postRepository.findById(id).get().getMediaUrl();
    }
//    @GetMapping("/user/{username}/posts")
//    public ResponseEntity<List<Post>> getOwnedPosts(@PathVariable String username, HttpSession session, HttpServletRequest request) {
//        userController.validateLogin(request);
//        System.out.println(userRepository.findById(1l).get());
//        long userId = userRepository.findUserByUsername(username).getId();
//        System.out.println(userId + "!!!!!!!!!!!!!!!!!!");
//        return null;//ResponseEntity.status(200).body(postRepository.findAllByUserId(userId));
//    }
    @GetMapping("/users/upvotes")
    public ResponseEntity<List<Post>> getUpvotedPosts(@RequestParam("id") long id, HttpServletRequest request) {
        userController.validateLogin(request);
        //return userRepository.findById(id).get().getUpvotedPosts();
        return null;
    }
    @GetMapping("/users/saved")
    public ResponseEntity<List<Post>> getUSavedPosts(@RequestParam("id") long id, HttpServletRequest request) {
        userController.validateLogin(request);
        //return userRepository.findById(id).get().getSavedPosts();
        return null;
    }
    @GetMapping("/users/comments")
    public ResponseEntity<List<Post>> getCommentedPosts(@RequestParam("id") long id, HttpServletRequest request) {
        userController.validateLogin(request);
        //return userRepository.findById(id).get().getCommentedPosts();
        return null;
    }
    @DeleteMapping("/posts/{id}/delete")
    public void deletePost(@PathVariable long id, HttpServletRequest request, HttpSession session) {
        userController.validateLogin(request);
        Post p = postRepository.findById(id).orElseThrow(() -> new NotFoundException("post with id=" + id + "is not existing"));
        if(p.getOwner().getId() == (Long)session.getAttribute(UserController.User_Id)) {//if current user is owner
            postRepository.deleteById(id);
        }
        else {
            throw new UnauthorizedException("Only the owner of the post can delete it!");
        }
    }
}
