package com.example.springproject.dto;


import com.example.springproject.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {

    private long id;
    private String username;
    private String profile_picture_url;


}
