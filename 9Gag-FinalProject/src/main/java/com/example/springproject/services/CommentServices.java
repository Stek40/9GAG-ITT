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
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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


//    public Comment addComment(@RequestBody CommentAddDto commentRequest, HttpServletRequest request) {
//        if (commentRequest.getText()== null && commentRequest.getMediaUrl() == null){
//            throw new BadRequestException("Please enter a comment");
//        }
//        Optional<Post> post = postRepository.findById(commentRequest.getPostId());
//        Optional<User> commentOwner = userRepository.findById((Long) request.getSession().getAttribute(UserController.User_Id));
//        if (post.isPresent()){
//            Comment comment = new Comment();
//            if (commentRequest.getText() != null){
//                comment.setText(commentRequest.getText());
//            }
//            if (commentRequest.getMediaUrl() != null){
//                comment.setMediaUrl(commentRequest.getMediaUrl());
//            }
//            comment.setUpvotes(0);
//            comment.setDownvotes(0);
//            comment.setCommentOwner(commentOwner.get());
//            comment.setDateTime(LocalDateTime.now());
//
//            comment.setPost(post.get());
//            commentRepository.save(comment);
//            return comment;
//
//        }
//        throw new NotFoundException("Post not found !");
//    }

    public Comment createComment(MultipartFile file, String text, long postId, long userId) {
        if (text == null && file == null){
            throw new BadRequestException("Please enter a comment");
        }
        Optional<Post> post = postRepository.findById(postId);
        Optional<User> commentOwner = userRepository.findById(userId);
        if (post.isPresent()){
            Comment comment = new Comment();
            if (file != null){
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                String name = System.nanoTime() +"."+ ext;
                try {
                    Files.copy(file.getInputStream(), new File("commentImages" + File.separator + name).toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                comment.setMediaUrl(name);
            }
            if (text != null){
                comment.setText(text);
            }

            comment.setUpvotes(0);
            comment.setDownvotes(0);
            comment.setCommentOwner(commentOwner.get());
            comment.setDateTime(LocalDateTime.now());
            comment.setPost(post.get());
            commentRepository.save(comment);
            post.get().getComments().add(comment);
            return comment;

        }
        throw new NotFoundException("Post not found !");

    }
}
