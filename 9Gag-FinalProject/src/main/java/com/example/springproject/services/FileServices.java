package com.example.springproject.services;

import com.example.springproject.controller.UserController;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileServices {

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;


    public File getFileFromPost(long postId) {
        String fileName = postRepository.getMediaUrlOfPostWithId(postId);
        return new File("media" + File.separator + "postMedia" + File.separator + fileName);
    }

    @SneakyThrows
    public void validateMediaType(MultipartFile multipartFile, boolean onlyPhotoAllowed) {
        Tika tika = new Tika();
        String detectedType = tika.detect(multipartFile.getInputStream());
        System.out.println(detectedType); // print check
        if(onlyPhotoAllowed && !detectedType.contains("image")) {
            throw new UnauthorizedException("This media type is not allowed.");
        }
        if(!onlyPhotoAllowed && !detectedType.contains("video") && !detectedType.contains("image")) {
            throw new UnauthorizedException("This media type is not allowed.");
        }
    }
}
