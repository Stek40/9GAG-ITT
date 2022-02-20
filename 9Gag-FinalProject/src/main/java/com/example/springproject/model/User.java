package com.example.springproject.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

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
    private String gender;
    @Column
    private LocalDate date_of_birth;
    @Column
    private boolean is_hidden;
    @Column
    private String profile_picture_url;
    @OneToMany(mappedBy = "owner")
    //@JsonManagedReference
    private Set<Post> posts;
    @OneToMany(mappedBy = "commentOwner")
    private Set<Comment> comments;

    @ManyToMany(mappedBy = "savedUser")
    private Set<Post> savedPosts;

}
