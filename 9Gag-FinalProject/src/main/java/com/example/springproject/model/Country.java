package com.example.springproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "countries")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Country {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String name;
}
