package com.example.springproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User commentOwner;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @Column
    private String text;
    @Column(name = "media_url")
    String mediaUrl;
    @Column
    private long upvotes;
    @Column
    private long downvotes;
    @Column
    private LocalDateTime dateTime;
    @ManyToMany()
    @JoinTable(
            name = "users_upvote_comments",
            joinColumns = @JoinColumn (name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> uppVoters;
    @ManyToMany()
    @JoinTable(
            name = "users_downvote_comments",
            joinColumns = @JoinColumn (name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> downVoters;

    //private Set<Comment> answers;

}
