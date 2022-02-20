package com.example.springproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentWithoutOwnerDto {

    String text;
    long id;
    String postId;
    long upvotes;
    long downvotes;




}
