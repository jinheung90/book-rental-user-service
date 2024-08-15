package com.example.project.user.dto;



import com.example.project.user.dto.UserAuthorityDto;
import com.example.project.user.dto.UserDto;
import com.example.project.user.dto.UserProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Schema(description = "로그인, 회원가입 완료")
public class LoginResponse {
    @Schema(description = "jwt 토큰")
    private String accessToken;
    private UserAuthorityDto userAuthorityDto;
    private UserProfileDto userProfileDto;
    private UserDto userDto;
}
