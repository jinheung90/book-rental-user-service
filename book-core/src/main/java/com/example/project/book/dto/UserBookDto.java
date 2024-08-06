package com.example.project.book.dto;

import com.example.project.book.entity.UserBookImage;
import com.example.project.common.enums.BookRentalStateType;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

import java.util.Comparator;
import java.util.List;


@Getter
@NoArgsConstructor
@Schema(description = "유저의 책 정보")
public class UserBookDto {

    @Schema(description = "아이디")
    private Long id;
    @Schema(description = "책 이름")
    private String name;
    @Schema(description = "상세 정보")
    private String detail;
    @Schema(description = "책 이미지")
    private List<UserBookImageDto> userBookImageDtos;
    @Schema(description = "책 상태")
    private BookRentalStateType state;
    @Schema(description = "유저 아이디")
    private Long userId;

    @QueryProjection
    public UserBookDto(
            Long id,
            String name,
            String detail,
            List<UserBookImage> userBookImages,
            BookRentalStateType state,
            Long userId
    ) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.state = state;
        this.userBookImageDtos = userBookImages.stream().map(UserBookImageDto::fromEntity).sorted(
                Comparator.comparing(UserBookImageDto::getOrder)
        ).toList();
        this.userId = userId;
    }
}
