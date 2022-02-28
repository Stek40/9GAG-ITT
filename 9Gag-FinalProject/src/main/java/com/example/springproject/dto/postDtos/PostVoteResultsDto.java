package com.example.springproject.dto.postDtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostVoteResultsDto {
    private long id;
    private int upvotes;
    private int downvotes;
}
