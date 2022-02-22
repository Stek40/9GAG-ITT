package com.example.springproject.dto.newDtos.comment;

import com.example.springproject.dto.CommentWithoutOwnerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@NoArgsConstructor
public class AllCommentsOnPostDto {

    List<CommentWithoutOwnerDto> comments;


}
