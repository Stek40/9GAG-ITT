package com.example.springproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private int categoryId;
    @Column
    private int upvotes;
    @Column
    private int downvotes;
    @Column
    private long userId;
    @Column
    private LocalDateTime uploadDate;
}
