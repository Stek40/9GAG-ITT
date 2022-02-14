package com.example.springproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
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
    private boolean show_sensitive_content;
    @Column
    private long country_id;
    @Column
    private char gender;
    @Column
    private LocalDate date_of_birth;
    @Column
    private boolean is_hidden;
    @Column
    private String profile_picture_url;




}
