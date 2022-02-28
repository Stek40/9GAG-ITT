package com.example.springproject.dto.commentDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
@Getter
@Setter
@NoArgsConstructor
public class CommentWithMediaDto {

    private String text;
    private File file;
}
