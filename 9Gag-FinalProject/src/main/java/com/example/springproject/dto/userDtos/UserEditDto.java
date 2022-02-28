package com.example.springproject.dto.userDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEditDto {

    private long id;
    private String email;
    private String password;
    private String emailPassword;
    private String newPassword;
    private String confirmNewPassword;
    private String newEmail;
    private String setGender;
    private String username;
    private String new_username;

}
