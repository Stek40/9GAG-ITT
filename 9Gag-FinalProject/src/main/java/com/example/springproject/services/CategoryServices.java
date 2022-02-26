package com.example.springproject.services;

import com.example.springproject.dto.newDtos.categoriesDto.CategoryDto;
import com.example.springproject.dto.newDtos.postDtos.DisplayPostDto;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Category;
import com.example.springproject.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServices {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PostServices postServices;
    @Autowired
    CategoryServices categoryServices;
    @Autowired
    private ModelMapper modelMapper;


    public Category getCategory(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("category with id=" + id + " doesn't exist"));
    }

    public List<DisplayPostDto> allPostsByCategory(long categoryId, boolean isByUpvotes) {
        Category c = categoryServices.getCategory(categoryId);
        List<DisplayPostDto> pDtos;
        if(isByUpvotes) {
            pDtos = postServices.PostToDisplayPostDtoConversionCollection(postServices.sortPostsByUpvotes(postServices.postsSetToList(c.getPosts())));
        } else {
            pDtos = postServices.PostToDisplayPostDtoConversionCollection(postServices.sortPostsByDate(postServices.postsSetToList(c.getPosts())));
        }
        return pDtos;
    }

    public List<CategoryDto> getAll() {
        List<Category> allCategories = categoryRepository.findAll();
        List<CategoryDto> cDtos = new ArrayList<>();
        for (Category c : allCategories) {
            cDtos.add(modelMapper.map(c, CategoryDto.class));
        }
        return cDtos;
    }
}
