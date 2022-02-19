package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class PostWithoutCommentPostDto {

    private String description;
    private long id;
    private Set<CommentWithOutPostDto> comments;
}
