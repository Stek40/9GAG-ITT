package com.example.springproject.dto.postDtos;

import com.example.springproject.dto.commentDtos.CommentWithOutPostDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class PostWithoutCommentPostDto implements Comparable<PostWithoutCommentPostDto> {

    private String description;
    private long id;
    private LocalDateTime uploadDate;
    private int upvotes;
    private int downvotes;
    private Set<CommentWithOutPostDto> comments;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostWithoutCommentPostDto that = (PostWithoutCommentPostDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(PostWithoutCommentPostDto o) {
        return o.getUploadDate().compareTo(this.getUploadDate());
    }
}
