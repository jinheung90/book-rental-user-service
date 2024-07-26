package com.example.project.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class UserReviewDto {
    private Long id;
    private String name;
    private String detail;
    private String imageUrl;
    private float starRating;

    @QueryProjection
    public UserReviewDto(
            Long id,
            String name,
            String detail,
            String imageUrl,
            float starRating
    ) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.imageUrl = imageUrl;
        this.starRating = starRating;
    }
}
