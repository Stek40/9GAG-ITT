package com.example.springproject.controller;

import com.example.springproject.dto.commentDtos.CommentWithMediaDto;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.services.FileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class FileController {

    @Autowired
    UserController userController;

    @Autowired
    FileServices fileServices;

    @GetMapping("/files/profilePicture/download")
    public void download(HttpServletResponse response, HttpServletRequest request) {
        userController.validateLogin(request);
        fileServices.downloadProfilePicture(response, request);
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
        return fileServices.downloadCommentMedia(cId, response);
    }
}
