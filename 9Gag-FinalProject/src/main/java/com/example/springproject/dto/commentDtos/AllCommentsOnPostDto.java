package com.example.springproject.dto.commentDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AllCommentsOnPostDto {

    List<CommentWithoutOwnerDto> comments;


}
