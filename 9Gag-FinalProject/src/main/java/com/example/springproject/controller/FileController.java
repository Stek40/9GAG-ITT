package com.example.springproject.controller;

import com.example.springproject.dto.CommentWithMediaDto;
import com.example.springproject.model.Comment;
import com.example.springproject.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class FileController {

    @Autowired
    CommentRepository repository;

    @GetMapping("/files/profilePicture/{filename}")
    public void download(@PathVariable String filename, HttpServletResponse response){
        File file = new File("uploads"+ File.separator + filename);
        try {
            Files.copy(file.toPath(),response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/files/comment")
    public ResponseEntity<CommentWithMediaDto> getComment(HttpServletResponse response){
        Comment comment = repository.getById(33L);

        File file = new File("commentImages"+ File.separator + comment.getMediaUrl());
        try {
            Files.copy(file.toPath(),response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = "Sadsa";

        CommentWithMediaDto commentWithMediaDto = new CommentWithMediaDto();
        commentWithMediaDto.setText(comment.getText());



        return ResponseEntity.ok(commentWithMediaDto);
    }

}
