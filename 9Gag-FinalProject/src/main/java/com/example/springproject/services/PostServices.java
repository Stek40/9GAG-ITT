package com.example.springproject.services;

import com.example.springproject.dto.PostDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Post;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Service
public class PostServices {

    private final String urlRegex = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\\\+~#?&//=]{2,256}\\\\.[a-z]{2,6}\\\\b([-a-zA-Z0-9@:%._\\\\+~#?&//=]*)";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Post create(String description, String mediaUrl, int categoryId, long userId) {

        if(description == null || description.isBlank() || description.length() <= 2) {
            throw new BadRequestException("post description is missing or is less than 3 symbols");
        }
        if(mediaUrl == null || mediaUrl.matches(urlRegex)){
            throw new BadRequestException("post media url is missing or is not correct");
        }
        if(categoryId <= 0 || !categoryRepository.existsById((long)categoryId)) {
            throw new NotFoundException("category with id=" + categoryId + " doesn't exist");
        }
        if(userId <= 0 || !userRepository.existsById(userId)) {
            throw new NotFoundException("user with id=" + userId + " doesn't exist");
        }
        Post p = new Post();
        p.setDescription(description);
        p.setMediaUrl(mediaUrl);
        p.setCategory(categoryRepository.getById((long) categoryId));
        p.setOwner(userRepository.getById(userId));

        p.setDownvotes(0);
        p.setUpvotes(0);
        p.setUploadDate(LocalDateTime.now());
        return p;
    }
}
