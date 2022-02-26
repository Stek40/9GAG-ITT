package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentWithoutOwnerDto {

    private String text;
    private long id;
    private String postId;
    private long upvotes;
    private long downvotes;
    private String mediaUrl;
    private LocalDateTime dateTime;


}
