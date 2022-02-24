package com.example.springproject.repositories;

import com.example.springproject.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository  extends JpaRepository<Post, Long> {
    @Query(
            value = "SELECT media_url FROM posts WHERE id = ?",
            nativeQuery = true)
    String getMediaUrlOfPostWithId(long id);
    @Query(
            value = "SELECT * FROM posts order by upload_date desc",
            nativeQuery = true)
    List<Post> getAllOrderByUploadDate();
    @Query(
            value = "SELECT * FROM 9gag.posts order by (2*upvotes - downvotes) desc",
            nativeQuery = true)
    List<Post> getAllOrderByUpvotes();
    @Query(
            value = "SELECT description FROM posts",
            nativeQuery = true)
    List<String> findAllPostDescriptions();
    @Query(
            value = "SELECT id FROM posts",
            nativeQuery = true)
    List<Integer> findAllPostIds();
}
