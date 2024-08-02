package com.example.project.book.dto;

import com.example.project.book.entity.UserBookImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserBookImageDto {
    private Long id;
    private String imageUrl;
    private Integer order;

    public static UserBookImageDto fromEntity(UserBookImage userBookImage) {
        return UserBookImageDto.builder()
                .imageUrl(userBookImage.getImageUrl())
                .id(userBookImage.getId())
                .order(userBookImage.getOrder())
                .build();
    }
}
