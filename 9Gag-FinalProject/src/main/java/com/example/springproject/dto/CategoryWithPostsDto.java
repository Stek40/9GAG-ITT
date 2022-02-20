package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class CategoryWithPostsDto {
    private long id;
    private String name;
    private Set<PostWithOwnerAndWithoutCategoryDto> posts;
}
