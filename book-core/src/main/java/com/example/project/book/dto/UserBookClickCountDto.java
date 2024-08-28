package com.example.project.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@NoArgsConstructor
public class UserBookClickCountDto {

    private Long userBookId;
    private Long count;

    public UserBookClickCountDto(Long userBookId, Long count) {
        this.count = count;
        this.userBookId = userBookId;
    }

    public void increaseCount() {
        count++;
    }
}
