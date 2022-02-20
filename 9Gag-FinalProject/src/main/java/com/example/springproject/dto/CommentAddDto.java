package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentAddDto {

    private long id;
    private long postId;
    private String text;
    private String mediaUrl;
    private long upvotes;
    private long downVotes;
    private LocalDateTime dateTime;
}
