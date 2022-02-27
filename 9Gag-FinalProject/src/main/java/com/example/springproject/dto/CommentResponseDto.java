package com.example.springproject.dto;

import com.example.springproject.model.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {

    private long id;
    private long userId;
    private long postId;
    private String text;
    private String mediaUrl;
    private long upvotes;
    private long downVotes;
    private LocalDateTime dateTime;

}
