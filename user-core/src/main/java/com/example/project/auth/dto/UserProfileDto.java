package com.example.project.auth.dto;

import com.example.project.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserProfileDto {

    private String profileImageUrl;
    private String nickName;
    private String address;

    public static UserProfileDto fromEntity(User user) {
        return UserProfileDto.builder()
                .profileImageUrl(user.getProfileImageUrl())
                .address(user.getAddress())
                .nickName(user.getNickName())
                .build();
    }
}
