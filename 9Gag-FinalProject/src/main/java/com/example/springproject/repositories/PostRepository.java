package com.example.springproject.repositories;

import com.example.springproject.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository  extends PagingAndSortingRepository<Post, Long> {

    Post getById(long id);

    @Query(
            value = "SELECT media_url FROM posts WHERE id = ?",
            nativeQuery = true)
    String getMediaUrlOfPostWithId(long id);
    @Query(
            value = "SELECT * FROM posts order by upload_date desc",
            nativeQuery = true)
    List<Post> getAllOrderByUploadDate(Pageable pageable);
    @Query(
            value = "SELECT * FROM 9gag.posts order by (2*upvotes - downvotes) desc",
            nativeQuery = true)
    List<Post> getAllOrderByUpvotes(Pageable pageable);
    @Query(
            value = "SELECT description FROM posts",
            nativeQuery = true)
    List<String> findAllPostDescriptions();
    @Query(
            value = "SELECT id FROM posts",
            nativeQuery = true)
    List<Integer> findAllPostIds();
    @Query(
            value = "SELECT * FROM posts WHERE category_id = ?",
            nativeQuery = true)
    List<Post> findAllByCategoryId(long id, Pageable pageable);
}
