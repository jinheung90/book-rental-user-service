package com.example.project.auth.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private UserAuthorityDto userAuthorityDto;
    private UserProfileDto userProfileDto;
    private UserDto userDto;
}
