package com.example.springproject.controller;

import com.example.springproject.dto.CommentWithMediaDto;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Comment;
import com.example.springproject.repositories.CommentRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.FileServices;
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
    @Autowired
    FileServices fileServices;

    @GetMapping("/files/profilePicture/download")
    public void download(HttpServletResponse response, HttpServletRequest request) {
        userController.validateLogin(request);
        String filename = userRepository.getUserByRequest(request).getProfile_picture_url();
        System.out.println(filename);
        File file = new File("media" + File.separator + "profilePictures" + File.separator + filename);
        try {
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new NotFoundException("Profile picture not found !");
        }
    }

    @GetMapping("/files/post/download")
    public void downloadPostMedia(@RequestParam(name = "postId") long postId, HttpServletResponse response, HttpServletRequest request) {
        File f = fileServices.getFileFromPost(postId);
        try {
            Files.copy(f.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new NotFoundException("Post media not found.");
        }
    }

    @GetMapping("/files/comment/download")
    public ResponseEntity<CommentWithMediaDto> getComment(@RequestParam(name = "commentId") long cId, HttpServletResponse response) {
        Optional<Comment> comment = repository.findById(cId);
        if (comment.isPresent()) {
            File file = new File("media" + File.separator + "commentImages" + File.separator + comment.get().getMediaUrl());
            try {
                Files.copy(file.toPath(), response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //String s = "Sadsa";
            CommentWithMediaDto commentWithMediaDto = new CommentWithMediaDto();
            commentWithMediaDto.setText(comment.get().getText());
            return ResponseEntity.ok(commentWithMediaDto);
        }
        throw new NotFoundException("Comment not found !");
    }
}
