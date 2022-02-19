package com.example.springproject.repositories;

import com.example.springproject.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository  extends JpaRepository<Post, Long> {
    //public List<Post> findAllByUserId(long userId);
}
