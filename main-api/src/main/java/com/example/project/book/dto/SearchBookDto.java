package com.example.project.book.dto;

import com.example.project.user.dto.UserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "유저가 가지고 있는 책과 프로필 정보")
public class SearchBookDto {
    @Schema(description = "유저의 책 정보")
    private UserBookDto userBookDto;
    @Schema(description = "유저 프로필 정보")
    private UserProfileDto userProfileDto;
}
