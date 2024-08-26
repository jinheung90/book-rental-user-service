package com.example.project.book.dto;

import com.example.project.book.store.entity.UserBookImage;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "유저의 책 이미지")
public class UserBookImageDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "이미지 url")
    private String imageUrl;

    @Schema(description = "순서 0~2")
    private Integer imageOrder;

    @QueryProjection
    public UserBookImageDto(
            Long id,
            String imageUrl,
            Integer imageOrder
    ) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageOrder = imageOrder;
    }

    public static UserBookImageDto fromEntity(UserBookImage userBookImage) {
        return UserBookImageDto.builder()
                .imageUrl(userBookImage.getImageUrl())
                .id(userBookImage.getId())
                .imageOrder(userBookImage.getImageOrder())
                .build();
    }
}
