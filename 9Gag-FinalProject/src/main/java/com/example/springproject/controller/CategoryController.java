package com.example.springproject.controller;

import com.example.springproject.dto.CategoryWithPostsDto;
import com.example.springproject.dto.CategoryWithoutPostsDto;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Category;
import com.example.springproject.model.Post;
import com.example.springproject.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class CategoryController {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CategoryRepository categoryRepository;

    @PostMapping("/category/addAllCategories")
    public void addAllCategories(@RequestBody ArrayList<Category> categories){
        categoryRepository.saveAll(categories);
    }
    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryWithPostsDto> getById(@PathVariable long id){
        Category c = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("category with id=" + id + " doesn't exist"));
        CategoryWithPostsDto cDto = modelMapper.map(c, CategoryWithPostsDto.class);

        return ResponseEntity.ok().body(cDto);
    }
}
