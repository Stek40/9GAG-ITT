package com.example.springproject.dto.userDtos;

import com.example.springproject.dto.postDtos.PostWithoutCommentPostDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserCreatedPostsByDate {

    private String full_name;
    private long id;
    private List<PostWithoutCommentPostDto> posts;// must be changed with new DTO
}
