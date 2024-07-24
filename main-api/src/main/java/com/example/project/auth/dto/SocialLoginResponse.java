package com.example.project.auth.dto;


import com.example.project.enums.LoginProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        LoginProvider loginProvider,
        String email,
        String socialId,
        Long userId
    ) {

        return SocialLoginResponse
            .builder()
            .accessToken(accessToken)
            .provider(loginProvider.name())
            .email(email)
            .userId(userId)
            .socialId(socialId)
            .build();
    }

    public static SocialLoginResponse emptyResponse() {
        return SocialLoginResponse.builder().build();
    }
}
