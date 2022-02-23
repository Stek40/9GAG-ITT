package com.example.springproject.services;

import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Category;
import com.example.springproject.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServices {

    @Autowired
    CategoryRepository categoryRepository;


    public Category getCategory(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("category with id=" + id + " doesn't exist"));
    }
}
