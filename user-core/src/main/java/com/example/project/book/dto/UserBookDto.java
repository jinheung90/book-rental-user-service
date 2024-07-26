package com.example.project.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class UserBookDto {
    private Long id;
    private String name;
    private String detail;
    private String imageUrl;

    @QueryProjection
    public UserBookDto(
            Long id,
            String name,
            String detail,
            String imageUrl
    ) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.imageUrl = imageUrl;
    }
}
