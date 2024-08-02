package com.example.project.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class JwtTokenDto {
    private String accessToken;
    private Long accessTokenExpired;
    private String refreshToken;
    private Long refreshTokenExpired;
}
