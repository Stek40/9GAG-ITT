package com.example.springproject.dto.newDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {

    private String description;
    private String mediaUrl;
    private int categoryId;
    private int upvotes;
    private int downvotes;
    private long userId;
    private LocalDateTime uploadDate;

}
