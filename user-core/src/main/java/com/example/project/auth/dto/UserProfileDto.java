package com.example.project.auth.dto;

import com.example.project.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "유저의 프로필 정보")
public class UserProfileDto {
    @Schema(description = "유저 아이디")
    private Long id;
    @Schema(description = "프로필 이미지")
    private String profileImageUrl;
    @Schema(description = "닉네임")
    private String nickName;
    @Schema(description = "주소")
    private String address;

    public static UserProfileDto fromEntity(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .address(user.getAddress())
                .nickName(user.getNickName())
                .build();
    }
}
