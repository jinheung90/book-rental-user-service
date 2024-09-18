package com.example.project.user.v2.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class JwtTokenDto {
    private String accessToken;
    private String accessTokenExpired;
}
