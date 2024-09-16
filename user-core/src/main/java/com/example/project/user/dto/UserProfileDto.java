package com.example.project.user.dto;

import com.example.project.user.entity.User;
import com.example.project.user.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "유저의 프로필 정보")
public class UserProfileDto {

    @Schema(description = "유저 아이디")
    protected Long id;

    @Schema(description = "프로필 이미지")
    protected String profileImageUrl;

    @Schema(description = "닉네임")
    protected String nickName;

    @Schema(description = "주소")
    protected List<UserAddressDto> addresses;

    public static UserProfileDto fromEntity(UserProfile userProfile) {
        return UserProfileDto.builder()
                .id(userProfile.getId())
                .addresses(UserAddressDto.fromEntityList(userProfile.getUser().getUserAddress()))
                .profileImageUrl(userProfile.getProfileImageUrl())
                .nickName(userProfile.getNickName())
                .build();
    }

    public static UserProfileDto fromEntityWithoutAddress(UserProfile userProfile) {
        return UserProfileDto.builder()
                .id(userProfile.getId())
                .addresses(new ArrayList<>())
                .profileImageUrl(userProfile.getProfileImageUrl())
                .nickName(userProfile.getNickName())
                .build();
    }
}
