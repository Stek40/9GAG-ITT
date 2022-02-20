package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserWithAllPost {

    private String full_name;
    private long id;
    private Set<PostWithoutCommentPostDto> postWithoutCommentPostDto;
}
