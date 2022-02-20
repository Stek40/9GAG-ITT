package com.example.springproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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
}
