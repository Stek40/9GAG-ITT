package com.example.springproject.dto.newDtos.user;


import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDto {

    private String full_name;
    private String username;
    private String about;
    private String email;
    private String password;
    private String confirmPassword;
    private boolean show_sensitive_content;
    private boolean hidden;
    private long country_id;
    private String gender;

    private String date_of_birth;

    private String profile_picture_url;
}
