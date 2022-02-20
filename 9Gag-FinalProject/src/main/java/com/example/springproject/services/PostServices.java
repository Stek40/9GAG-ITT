package com.example.springproject.services;

import com.example.springproject.dto.PostDto;
import com.example.springproject.dto.UserWithAllSavedPostDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostServices {


    private final String urlRegex = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\\\+~#?&//=]{2,256}\\\\.[a-z]{2,6}\\\\b([-a-zA-Z0-9@:%._\\\\+~#?&//=]*)";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Post create(String description, String mediaUrl, int categoryId, long userId) {

        if(description == null || description.isBlank() || description.length() <= 2) {
            throw new BadRequestException("post description is missing or is less than 3 symbols");
        }
        if(mediaUrl == null || mediaUrl.matches(urlRegex)){
            throw new BadRequestException("post media url is missing or is not correct");
        }
        if(categoryId <= 0 || !categoryRepository.existsById((long)categoryId)) {
            throw new NotFoundException("category with id=" + categoryId + " doesn't exist");
        }
        if(userId <= 0 || !userRepository.existsById(userId)) {
            throw new NotFoundException("user with id=" + userId + " doesn't exist");
        }
        Post p = new Post();
        p.setDescription(description);
        p.setMediaUrl(mediaUrl);
        p.setCategory(categoryRepository.getById((long) categoryId));
        p.setOwner(userRepository.getById(userId));

        p.setDownvotes(0);
        p.setUpvotes(0);
        p.setUploadDate(LocalDateTime.now());
        return p;
    }
    public Post votePost(boolean isUpvote, long postId, long userId) {
        Post p = this.getPostById(postId);
        User u = userRepository.getById(userId);

        if(isUpvote) {
            if(u.getUpvotedPosts().contains(p)) {//already upvoted
                u.getUpvotedPosts().remove(p);
                p.getUpvoters().remove(u);
                p.setUpvotes(p.getUpvotes() - 1);
            }
            else if(u.getDownvotedPosts().contains(p)) {//already downvoted
                u.getDownvotedPosts().remove(p);
                p.getDownvoters().remove(u);
                p.setDownvotes(p.getDownvotes() - 1);
                p.getUpvoters().add(u);
                u.getUpvotedPosts().add(p);
                p.setUpvotes(p.getUpvotes() + 1);
            }
            else {
                p.getUpvoters().add(u);
                u.getUpvotedPosts().add(p);
                p.setUpvotes(p.getUpvotes() + 1);
            }
        }
        else {
            if(u.getDownvotedPosts().contains(p)) {//already downvoted
                u.getDownvotedPosts().remove(p);
                p.getDownvoters().remove(u);
                p.setDownvotes(p.getDownvotes() - 1);
            }
            else if(u.getUpvotedPosts().contains(p)) {//already upvoted
                u.getUpvotedPosts().remove(p);
                p.getUpvoters().remove(u);
                p.setUpvotes(p.getUpvotes() - 1);
                p.getDownvoters().add(u);
                u.getDownvotedPosts().add(p);
                p.setDownvotes(p.getDownvotes() + 1);
            }
            else {
                p.getDownvoters().add(u);
                u.getDownvotedPosts().add(p);
                p.setDownvotes(p.getDownvotes() + 1);
            }
        }
        return p;
    }
    public Post getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("post with id=" + postId + "is not existing"));
    }

    public User savedPost(int postId, long userId) {
        Optional<Post> post = postRepository.findById((long) postId);
        User user = userRepository.getById(userId);
        if (post.isPresent()) {
            if (user.getSavedPosts().contains(post.get())) {
                throw new BadRequestException("User already saved this post !");
            }
            post.get().getSavedUser().add(user);
            postRepository.save(post.get());
            return user;
        }
        throw new NotFoundException("Post not found !");
    }

    public User unSavedPost(int postId, Long userId) {
        Optional<Post> post = postRepository.findById((long) postId);
        User user = userRepository.getById(userId);
        if (post.isPresent()) {
            if (user.getSavedPosts().contains(post.get())) {
                user.getSavedPosts().remove(post.get());
                post.get().getSavedUser().remove(user);
                postRepository.save(post.get());
                return user;
            }
        }
        throw new NotFoundException("Post not found !");
    }
}
