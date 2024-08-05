package com.example.project.book.dto;

import com.example.project.auth.dto.UserProfileDto;
import com.example.project.auth.entity.User;
import com.example.project.book.entity.UserBookImage;
import com.example.project.enums.BookRentalStateType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;


@Getter
@NoArgsConstructor
public class UserBookDto {

    private Long id;
    private String name;
    private String detail;
    private List<UserBookImageDto> userBookImageDtos;
    private BookRentalStateType state;
    private UserProfileDto userProfileDto;

    @QueryProjection
    public UserBookDto(
            Long id,
            String name,
            String detail,
            List<UserBookImage> userBookImages,
            BookRentalStateType state,
            User user
    ) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.state = state;
        this.userBookImageDtos = userBookImages.stream().map(UserBookImageDto::fromEntity).sorted(
                Comparator.comparing(UserBookImageDto::getOrder)
        ).toList();
        this.userProfileDto = UserProfileDto.fromEntity(user);
    }
}
