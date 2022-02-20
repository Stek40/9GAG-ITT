package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentWithOutPostDto {

    private String text;
    private LocalDateTime localDateTime;
    private int ownerId;
}
