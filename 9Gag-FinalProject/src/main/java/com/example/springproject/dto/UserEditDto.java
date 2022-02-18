package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEditDto {

    private long id;
    private String password;
    private String newPassword;
    private String confirmNewPassword;
    private String newEmail;
    private String setGender;
    private String username;

}
