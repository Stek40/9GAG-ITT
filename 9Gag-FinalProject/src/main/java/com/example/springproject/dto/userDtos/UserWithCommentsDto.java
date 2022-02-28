package com.example.springproject.dto.userDtos;

import com.example.springproject.dto.commentDtos.CommentWithoutOwnerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class UserWithCommentsDto {


    private String username;
    private List<CommentWithoutOwnerDto> comments;
}
