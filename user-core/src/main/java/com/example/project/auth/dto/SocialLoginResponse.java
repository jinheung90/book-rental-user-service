package com.example.project.auth.dto;


import com.example.project.auth.entity.User;
import com.example.project.auth.entity.UserSecurity;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialLoginResponse {
    private String accessToken;
    private String email;
    private String socialId;
    private Long userId;
    private String provider;

    public static SocialLoginResponse from(
        String accessToken,
        UserSecurity userSecurity
    ) {

        User user = userSecurity.getUser();
        return SocialLoginResponse
            .builder()
            .accessToken(accessToken)
            .provider(userSecurity.getProvider().name())
            .email(userSecurity.getEmail())
            .userId(user.getId())
            .socialId(userSecurity.getSocialMemberId())
            .build();
    }

    public static SocialLoginResponse emptyResponse(
    ) {
        return SocialLoginResponse.builder().build();
    }
}
