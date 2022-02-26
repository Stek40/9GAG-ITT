package com.example.springproject.controller;

import com.example.springproject.dto.newDtos.categoriesDto.CategoryDto;
import com.example.springproject.dto.newDtos.postDtos.DisplayPostDto;
import com.example.springproject.model.Category;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.services.CategoryServices;
import com.example.springproject.services.PostServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PostServices postServices;
    @Autowired
    CategoryServices categoryServices;

    @PostMapping("/category/addAllCategories")
    public void addAllCategories(@RequestBody ArrayList<Category> categories){
        categoryRepository.saveAll(categories);
    }
    @GetMapping("/category/{id}")
    public ResponseEntity<List<DisplayPostDto>> getAllPostsByCategory(@PathVariable long id, @RequestParam("sort_by_upvotes") boolean isByUpvotes){
        //no login
        List<DisplayPostDto> pDtos = categoryServices.allPostsByCategory(id, isByUpvotes);
        return ResponseEntity.ok().body(pDtos);
    }
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryServices.getAll());
    }
}
