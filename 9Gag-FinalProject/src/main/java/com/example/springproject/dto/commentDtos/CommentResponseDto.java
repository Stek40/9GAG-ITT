package com.example.springproject.dto.commentDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {

    private long id;
    private long userId;
    private long postId;
    private String text;
    private String mediaUrl;
    private long upvotes;
    private long downVotes;
    private LocalDateTime dateTime;

}
