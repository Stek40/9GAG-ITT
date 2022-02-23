package com.example.springproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Table(name = "categories")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @OneToMany(mappedBy = "category")
    private Set<Post> posts;
    @ManyToMany()
    @JoinTable(
            name = "users_add_favourite_categories",
            joinColumns = @JoinColumn (name ="categories_id" ),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;


}
