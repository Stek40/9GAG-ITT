package com.example.springproject.dto.userDtos;

import com.example.springproject.dto.postDtos.PostWithoutOwnerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserWithAllSavedPostDto {

    private long id;
    private String full_name;
    private List<PostWithoutOwnerDto> savedPosts; //to be changed with new DTO
}
