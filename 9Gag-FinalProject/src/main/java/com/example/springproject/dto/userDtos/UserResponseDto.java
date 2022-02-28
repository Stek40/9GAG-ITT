package com.example.springproject.dto.userDtos;


import com.example.springproject.dto.categoryDtos.CategoryDto;
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
    private boolean show_sensitive_content;
    private boolean hidden;
    private Set<CategoryDto> categories;
}
