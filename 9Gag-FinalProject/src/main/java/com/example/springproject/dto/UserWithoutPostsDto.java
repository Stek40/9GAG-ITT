package com.example.springproject.dto;

import com.example.springproject.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserWithoutPostsDto {

    private long id;
    private String full_name;
    private String username;
    private String about;
    private String email;
    private String password;
    private boolean show_sensitive_content;
    private long country_id;
    private String gender;
    private LocalDate date_of_birth;
    private boolean is_hidden;
    private String profile_picture_url;

}
