package com.example.springproject.repositories;

import com.example.springproject.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository  extends JpaRepository<Post, Long> {
    //public List<Post> findAllByUserId(long userId);

    @Query(
            value = "SELECT description FROM posts u ",
            nativeQuery = true)
    List<String> findAllPostDescriptions();
    @Query(
            value = "SELECT id FROM posts u ",
            nativeQuery = true)
    List<Integer> findAllPostIds();
}
