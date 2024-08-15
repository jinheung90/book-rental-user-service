package com.example.project.book.dto;

import com.example.project.book.entity.UserBook;
import com.example.project.book.entity.UserBookImage;
import com.example.project.common.enums.BookRentalStateType;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public static UserBookDto fromEntity(UserBook userBook) {
        return UserBookDto.builder()
                .id(userBook.getId())
                .userId(userBook.getUserId())
                .userBookImageDtos(
                        userBook.getImages().stream().map(
                                UserBookImageDto::fromEntity
                        ).toList()
                ).detail(userBook.getDetail())
                .name(userBook.getBook().getName())
                .state(userBook.getState())
                .build();
    }

}
