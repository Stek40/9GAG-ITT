package com.example.springproject.dto.postDtos;

import com.example.springproject.dto.categoryDtos.CategoryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class PostWithoutOwnerDto {
    private long id;
    private String description;
    private String mediaUrl;
    private int upvotes;
    private int downvotes;
    private LocalDateTime uploadDate;
    private CategoryDto category;
}
