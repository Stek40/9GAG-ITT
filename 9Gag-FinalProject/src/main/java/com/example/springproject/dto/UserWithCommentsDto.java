package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor

public class UserWithCommentsDto {


    private String username;
    private Set<CommentWithoutOwnerDto> comments;
}
