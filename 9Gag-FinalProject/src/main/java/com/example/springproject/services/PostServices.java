package com.example.springproject.services;

import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Post;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostServices {

    private final String urlRegex = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\\\+~#?&//=]{2,256}\\\\.[a-z]{2,6}\\\\b([-a-zA-Z0-9@:%._\\\\+~#?&//=]*)";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Post create(Post p) {
        String pDescription = p.getDescription();
        if(pDescription == null || pDescription.isBlank() || pDescription.length() <= 2) {
            throw new BadRequestException("post description is missing or is less than 3 symbols");
        }
        if(p.getMediaUrl() == null || p.getMediaUrl().matches(urlRegex)){
            throw new BadRequestException("post media url is missing or is not correct");
        }
        if(p.getUserId() <= 0 || !userRepository.existsById(p.getUserId())) {
            throw new NotFoundException("user with id=" + p.getUserId() + " doesn't exist");
        }
        if(p.getCategoryId() <= 0 || !categoryRepository.existsById((long) p.getCategoryId())) {
            throw new NotFoundException("category with id=" + p.getCategoryId() + " doesn't exist");
        }

        p.setDownvotes(0);
        p.setUpvotes(0);
        p.setUploadDate(LocalDateTime.now());
        return p;
    }
}
