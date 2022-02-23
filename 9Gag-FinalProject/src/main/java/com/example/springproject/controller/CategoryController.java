package com.example.springproject.controller;

import com.example.springproject.dto.CategoryWithPostsDto;
import com.example.springproject.dto.CategoryWithoutPostsDto;
import com.example.springproject.dto.newDtos.postDtos.DisplayPostDto;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Category;
import com.example.springproject.model.Post;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
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
    public ResponseEntity<List<DisplayPostDto>> getById(@PathVariable long id){
        Category c = categoryServices.getCategory(id);
        List<DisplayPostDto> pDtos = postServices.PostToDisplayPostDtoConversionCollection(postServices.sortPostsByDate(postServices.postsSetToList(c.getPosts())));
        return ResponseEntity.ok().body(pDtos);
    }
}
