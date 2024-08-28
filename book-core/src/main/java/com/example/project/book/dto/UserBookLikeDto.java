package com.example.project.book.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBookLikeDto {
    private Long userId;
    private Long userBookId;
    private boolean activity;
    private Instant updatedAt;
}
