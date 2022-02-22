package com.example.springproject.dto.newDtos.user;

import com.example.springproject.dto.PostWithoutCommentPostDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class UserCreatedPostsByDate {

    private String full_name;
    private long id;
    private List<PostWithoutCommentPostDto> posts;
}
