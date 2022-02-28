package com.example.springproject.dto.userDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDtoRegister {
    private long id;
    private String full_name;
    private String username;
    private String about;
    private String email;
    private boolean show_sensitive_content;
    private long country_id;
    private String gender;
    private LocalDate date_of_birth;
    private boolean isHidden;
    private String profile_picture_url;

}
