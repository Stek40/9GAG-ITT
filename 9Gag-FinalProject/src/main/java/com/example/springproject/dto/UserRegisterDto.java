package com.example.springproject.dto;


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
    @Column
    private String username;
    @Column
    private String about;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String confirmPassword;
    @Column
    private boolean show_sensitive_content;
    @Column
    private long country_id;
    @Column
    private String gender;
    @Column
    private LocalDate date_of_birth;
    @Column
    private boolean is_hidden;
    @Column
    private String profile_picture_url;
}
