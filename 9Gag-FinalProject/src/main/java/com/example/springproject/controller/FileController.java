package com.example.springproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class FileController {
    @GetMapping("/files/profilePicture/{filename}")
    public void download(@PathVariable String filename, HttpServletResponse response){
        File file = new File("uploads"+ File.separator + filename);
        try {
            Files.copy(file.toPath(),response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
