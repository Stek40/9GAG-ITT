package com.example.springproject.services;

import com.example.springproject.controller.UserController;
import com.example.springproject.dto.CommentAddDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Comment;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CommentRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentServices {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;


    public Comment comment (){
     return null;
    }


    public Comment addComment(@RequestBody CommentAddDto commentRequest, HttpServletRequest request) {
        if (commentRequest.getText()== null && commentRequest.getMediaUrl() == null){
            throw new BadRequestException("Please enter a comment");
        }
        Optional<Post> post = postRepository.findById(commentRequest.getPostId());
        Optional<User> commentOwner = userRepository.findById((Long) request.getSession().getAttribute(UserController.User_Id));
        if (post.isPresent()){
            Comment comment = new Comment();
            comment.setUpvotes(0);
            comment.setDownvotes(0);
            comment.setCommentOwner(commentOwner.get());
            comment.setDateTime(LocalDateTime.now());
            comment.setText(commentRequest.getText());
            comment.setPost(post.get());
            commentRepository.save(comment);
            return comment;

        }
        throw new NotFoundException("Post not found !");
    }
}
