package com.example.springproject.controller;

import com.example.springproject.model.Category;
import com.example.springproject.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

    @PostMapping("/category/addAllCategories")
    public void addAllCategories(@RequestBody ArrayList<Category> categories){
        categoryRepository.saveAll(categories);
    }
    @GetMapping("/category/{id}")
    public String getById(@PathVariable long id){


        return categoryRepository.getById(id).getName();
    }
}
