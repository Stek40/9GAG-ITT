package com.example.springproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

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
    @Column(name = "is_hidden")
    private boolean hidden;
    @Column
    private String profile_picture_url;
    @Column
    private String token;
    @Column
    private boolean isVerified;
    @OneToMany(mappedBy = "owner")
    private Set<Post> posts;
    @OneToMany(mappedBy = "commentOwner")
    private Set<Comment> comments;

    @ManyToMany(mappedBy = "upvoters")
    private Set<Post> upvotedPosts;
    @ManyToMany(mappedBy = "downvoters")
    private Set<Post> downvotedPosts;

    @ManyToMany(mappedBy = "savedUser")
    private Set<Post> savedPosts;

    @ManyToMany(mappedBy = "uppVoters")
    private Set<Comment> upVoteComments;
    @ManyToMany(mappedBy = "downVoters")
    private Set<Comment> downVote;

    @ManyToMany(mappedBy = "users")
    private Set<Category> categories;


}
