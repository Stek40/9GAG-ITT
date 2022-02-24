package com.example.springproject.dto.newDtos.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDto {


    private String username;
    private String password;
}
