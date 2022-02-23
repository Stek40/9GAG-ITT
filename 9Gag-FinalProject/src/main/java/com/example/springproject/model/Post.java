package com.example.springproject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String description;
    @Column
    private String mediaUrl;
    @Column
    private int upvotes;
    @Column
    private int downvotes;
    @Column
    private LocalDateTime uploadDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "users_upvote_posts",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User>  upvoters;
    @ManyToMany
    @JoinTable(
            name = "users_downvote_posts",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User>  downvoters;
    @ManyToMany()
    @JoinTable(
            name = "users_saved_posts",
            joinColumns = @JoinColumn (name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> savedUser;
}
