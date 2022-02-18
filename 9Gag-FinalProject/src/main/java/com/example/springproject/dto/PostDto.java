package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class PostDto {

    private long id;
    private String description;
    private String mediaUrl;
    private int categoryId;
    private int upvotes;
    private int downvotes;
    private long userId;
    private LocalDateTime uploadDate;
}
