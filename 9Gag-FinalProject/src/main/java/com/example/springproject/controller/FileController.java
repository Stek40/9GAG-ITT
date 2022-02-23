package com.example.springproject.controller;

import com.example.springproject.dto.CommentWithMediaDto;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Comment;
import com.example.springproject.repositories.CommentRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@RestController
public class FileController {

    @Autowired
    CommentRepository repository;
    @Autowired
    UserController userController;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/files/profilePicture")
    public void download(HttpServletResponse response,HttpServletRequest request){
        userController.validateLogin(request);
        String filename = userRepository.getById((Long) request.getSession().getAttribute(UserController.User_Id)).getProfile_picture_url();

        File file = new File("uploads"+ File.separator + filename);
        try {
            Files.copy(file.toPath(),response.getOutputStream());
        } catch (IOException e) {
            throw new NotFoundException("Profile picture not found !");
        }
    }
    @SneakyThrows
    @GetMapping("/files/{postId}/download")
    public void downloadPostMedia(@PathVariable long postId, HttpServletResponse response, HttpServletRequest request){
        userController.validateLogin(request);
        String fileName = postRepository.getMediaUrlOfPostWithId(postId);
        File f = new File("media" + File.separator + "postMedia" + File.separator + fileName);
        Files.copy(f.toPath(),response.getOutputStream());
    }
    @GetMapping("/files/comment")
    public ResponseEntity<CommentWithMediaDto> getComment(@RequestParam (name = "commentId") long cId, HttpServletResponse response){
        Optional<Comment> comment = repository.findById(cId);
        if (comment.isPresent()) {
        File file = new File("commentImages"+ File.separator + comment.get().getMediaUrl());
        try {
            Files.copy(file.toPath(),response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = "Sadsa";

             CommentWithMediaDto commentWithMediaDto = new CommentWithMediaDto();
             commentWithMediaDto.setText(comment.get().getText());
             return ResponseEntity.ok(commentWithMediaDto);

         }
         throw new NotFoundException("Comment not found !");



    }

}
