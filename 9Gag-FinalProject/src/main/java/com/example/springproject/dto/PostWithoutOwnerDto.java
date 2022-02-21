package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PostWithoutOwnerDto {

    private long id;
    private String description;
    private String mediaUrl;
    private int categoryId;
    private String categoryName;
    private int upvotes;
    private int downvotes;
    private Set<CommentWithOutPostDto> comments;

    //private long userId;
    private LocalDateTime uploadDate;
}