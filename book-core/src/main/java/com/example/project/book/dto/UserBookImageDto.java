package com.example.project.book.dto;

import com.example.project.book.entity.UserBookImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "유저의 책 이미지")
public class UserBookImageDto {

    @Schema(description = "id")
    private Long id;
    @Schema(description = "이미지 url")
    private String imageUrl;
    @Schema(description = "순서 1~3")
    private Integer order;

    public static UserBookImageDto fromEntity(UserBookImage userBookImage) {
        return UserBookImageDto.builder()
                .imageUrl(userBookImage.getImageUrl())
                .id(userBookImage.getId())
                .order(userBookImage.getOrder())
                .build();
    }
}
