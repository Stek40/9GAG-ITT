package com.example.springproject.dto;

import com.example.springproject.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class UserWithPostsDto {

    private String username;
    private Set<PostWithoutOwnerDto> posts;
}
