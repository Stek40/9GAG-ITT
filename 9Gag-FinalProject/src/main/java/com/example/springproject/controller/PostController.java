package com.example.springproject.controller;

import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Post;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.PostServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/new_post")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<Post> createPost(@RequestBody Post p, HttpSession session) {
        //session.isNew()
        //if logged

        p = postServices.create(p);
        postRepository.save(p);

        return ResponseEntity.status(HttpStatus.OK).body(p);
    }

    @PutMapping("/save_post")
    //if logged
    public void savePost(@RequestBody Post p) {
        if(false) {//check in Users_saved_posts if already saved

        }
    }

    @PutMapping("/posts/{id}/upvote")
    public ResponseEntity<Integer> upvoteComment(@PathVariable long id, HttpServletResponse resp) {
        Post p = postRepository.getById(id);
        int totalUpvotes = p.getUpvotes();
        if(false) { //check in users_upvote_posts if already upvoted
            //delete entry in users_upvote_posts
            p.setUpvotes(totalUpvotes-- - 1);
        }else{
            p.setUpvotes(totalUpvotes++ + 1);
        }
        postRepository.save(p);
        resp.addIntHeader("Upvotes", totalUpvotes);
        return ResponseEntity.status(200).build();
    }

    @PutMapping("/posts/{id}/downvote")
    public ResponseEntity<Integer> downvoteComment(@PathVariable long id, HttpServletResponse resp) {
        Post p = postRepository.getById(id);
        p.setDownvotes(p.getDownvotes() + 1);
        postRepository.save(p);
        resp.addIntHeader("Downvotes", p.getDownvotes());
        return ResponseEntity.status(200).build();
    }
    @GetMapping("/all_posts")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    @GetMapping("/posts/{id}")
    public Post getPostById(@PathVariable long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("post with id=" + id + " doesn't exist"));
        /*if(!postRepository.existsById(id)) {
            throw new NotFoundException("post with id=" + id + " doesn't exist");
        }
        return postRepository.getById(id);
         */
    }
    @GetMapping("/posts/{id}/download")
    public String downloadPostMedia(@PathVariable long id) {
        if(!postRepository.existsById(id)) {
            throw new NotFoundException("post with id=" + id + " doesn't exist");
        }
        return postRepository.findById(id).get().getMediaUrl();
    }
    @GetMapping("/user/{username}/posts")
    public ResponseEntity<List<Post>> getOwnedPosts(@PathVariable String username) {
        System.out.println(userRepository.findById(1l).get());
        long userId = userRepository.findUserByUsername(username).getId();
        System.out.println(userId + "!!!!!!!!!!!!!!!!!!");
        return ResponseEntity.status(200).body(postRepository.findAllByUserId(userId));
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
        if(p.getUserId() == 1) {// current user id
            postRepository.delete(p);
        }
    }
}
