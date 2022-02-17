package com.example.springproject.controller;

import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Post;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class PostController {

    private final String urlRegex = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\\\+~#?&//=]{2,256}\\\\.[a-z]{2,6}\\\\b([-a-zA-Z0-9@:%._\\\\+~#?&//=]*)";

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/new_post")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<Post> createPost(@RequestBody Post p, HttpSession session) {
        //session.isNew()
        //if logged
        if(p.getDescription() == null || p.getDescription().length() <= 2) {
            throw new BadRequestException("post description is missing or is less than 3 symbols");
        }
        if(p.getMediaUrl() == null || p.getMediaUrl().matches(urlRegex)){
            throw new BadRequestException("post media url is missing or is not correct");
        }
        if(p.getUserId() <= 0 || !userRepository.existsById(p.getUserId())) {
            throw new NotFoundException("user with id=" + p.getUserId() + " doesn't exist");
        }
        if(p.getCategoryId() <= 0 || !categoryRepository.existsById((long) p.getCategoryId())) {
            throw new NotFoundException("category with id=" + p.getCategoryId() + " doesn't exist");
        }

        p.setDownvotes(0);
        p.setUpvotes(0);
        p.setUploadDate(LocalDateTime.now());

        postRepository.save(p);
        System.out.println(p.getId());
        return ResponseEntity.status(HttpStatus.OK).body(p);
    }

    @PutMapping("/save_post")
    //if logged
    public void savePost(@RequestBody Post p) {
        if(false) {//check in Users_saved_posts if already saved

        }
    }

    @PutMapping("/posts/{id}/upvote")
    public ResponseEntity<Integer> upvoteComment(@PathVariable long id) {
        Post p = postRepository.getById(id);
        int totalUpvotes = p.getUpvotes();
        if(false) { //check in users_upvote_posts if already upvoted
            //delete entry in users_upvote_posts
            p.setUpvotes(totalUpvotes-- - 1);
        }else{
            p.setUpvotes(totalUpvotes++ + 1);
        }
        postRepository.save(p);
        return ResponseEntity.status(200).body(totalUpvotes);
    }

    @PutMapping("/posts/{id}/downvote")
    public ResponseEntity<Integer> downvoteComment(@PathVariable long id) {
        Post p = postRepository.getById(id);
        p.setDownvotes(p.getDownvotes() + 1);
        postRepository.save(p);
        return ResponseEntity.status(200).body(p.getDownvotes());
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
        //return postRepository.findById(id).orElseThrow()
        if(!postRepository.existsById(id)) {
            throw new NotFoundException("post with id=" + id + " doesn't exist");
        }
        return postRepository.findById(id).get().getMediaUrl();
    }
    @GetMapping("/user/{username}/upvotes")
    public List<Post> getUpvotedPosts(@PathVariable String userName) {
        return null;
    }
    @GetMapping("/user/{username}/saved")
    public List<Post> getUSavedPosts(@PathVariable String userName) {
        return null;
    }
    @GetMapping("/user/{username}/comments")
    public List<Post> getCommentedPosts(@PathVariable String userName) {
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
