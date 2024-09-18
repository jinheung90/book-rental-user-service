package com.example.project.user.v2.dto;



import com.example.project.user.dto.UserAddressDto;
import com.example.project.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;


@Getter
@AllArgsConstructor
@Schema(description = "로그인, 회원가입 완료")
@Builder
public class LoginResponse {
    private String accessToken;
    private UserDto userDto;
}
