package com.example.springproject.controller;

import com.example.springproject.dto.AddPostDto;
import com.example.springproject.dto.PostDto;
import com.example.springproject.dto.PostWithOwnerDto;
import com.example.springproject.dto.UserWithoutPostsDto;
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



    @GetMapping("/posts/getById/{id}")
    public Post getPostById(@PathVariable int id){
        return postRepository.getById((long) id);
    }

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
    public ResponseEntity<PostDto> votePost(@PathVariable long id, HttpServletResponse resp, HttpServletRequest req) {

        Post p = postRepository.getById(id);
        if(req.getRequestURI().contains("up")) {
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

        return ResponseEntity.ok(postRepository.findAll());
    }
    @GetMapping("/posts/{id}")
    public PostWithOwnerDto getPostById(@PathVariable long id) {
        Post p = postRepository.getById(id);
        PostWithOwnerDto pDto = new PostWithOwnerDto();
        pDto.setDescription(p.getDescription());
        pDto.setId(p.getId());
        pDto.setCategoryId(p.getCategoryId());
        pDto.setMediaUrl(p.getMediaUrl());
        pDto.setDownvotes(p.getDownvotes());
        pDto.setUpvotes(p.getUpvotes());
        pDto.setUploadDate(p.getUploadDate());
        pDto.setUserId(p.getOwner().getId());
        pDto.setOwner(modelMapper.map(p.getOwner(), UserWithoutPostsDto.class));
        return pDto;
        //return ResponseEntity.ok(postRepository.findById(id).orElseThrow(() -> new NotFoundException("post with id=" + id + " doesn't exist")));
    }
    @GetMapping("/posts/{id}/download")
    public String downloadPostMedia(@PathVariable long id) {
        if(!postRepository.existsById(id)) {
            throw new NotFoundException("post with id=" + id + " doesn't exist");
        }
        return postRepository.findById(id).get().getMediaUrl();
    }
    @GetMapping("/user/{username}/posts")
    public ResponseEntity<List<Post>> getOwnedPosts(@PathVariable String username, HttpSession session) {
        System.out.println(userRepository.findById(1l).get());
        long userId = userRepository.findUserByUsername(username).getId();
        System.out.println(userId + "!!!!!!!!!!!!!!!!!!");
        return null;//ResponseEntity.status(200).body(postRepository.findAllByUserId(userId));
    }
    @GetMapping("/user/{username}/upvotes")
    public ResponseEntity<List<Post>> getUpvotedPosts(@PathVariable String username) {
        long userId = userRepository.findUserByUsername(username).getId();
        return ResponseEntity.status(200).build();
    }
    @GetMapping("/user/{username}/saved")
    public List<Post> getUSavedPosts(@PathVariable String username) {
        return null;
    }
    @GetMapping("/user/{username}/comments")
    public List<Post> getCommentedPosts(@PathVariable String username) {
        return null;
    }
    @DeleteMapping("/post/{id}/delete_post")
    public void deletePost(@PathVariable long id) {
        Post p = postRepository.findById(id).orElseThrow(() -> new NotFoundException("post with id=" + id + "is already deleted"));
        if(p.getOwner().getId() == 1) {// current user id
            postRepository.delete(p);
        }
    }
}
