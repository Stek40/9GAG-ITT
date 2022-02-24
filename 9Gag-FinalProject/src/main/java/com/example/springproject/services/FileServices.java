package com.example.springproject.services;

import com.example.springproject.controller.UserController;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

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
}
