package com.example.springproject.dto.commentDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentWithOutPostDto {
    private long id;
    private String text;
    private LocalDateTime localDateTime;
    private int ownerId;
    private int upVotes;
    private int downVotes;


}
