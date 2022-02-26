package com.example.springproject.dto.newDtos.postDtos;

import com.example.springproject.dto.PostWithoutCommentPostDto;
import com.example.springproject.dto.newDtos.categoriesDto.CategoryDto;
import com.example.springproject.model.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class DisplayPostDto {

    private long id;
    private String description;
    private String mediaUrl;
    private int upvotes;
    private int downvotes;
    private long userId;
    private LocalDateTime uploadDate;
    private CategoryDto category;

}
