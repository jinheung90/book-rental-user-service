package com.example.project.book.dto;

import com.example.project.book.entity.UserBookImage;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class UserBookDto {

    private Long id;
    private String name;
    private String detail;
    private List<UserBookImageDto> userBookImageDtos;

    @QueryProjection
    public UserBookDto(
            Long id,
            String name,
            String detail,
            List<UserBookImage> userBookImages
    ) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.userBookImageDtos = userBookImages.stream().map(UserBookImageDto::fromEntity).toList();
    }
}
