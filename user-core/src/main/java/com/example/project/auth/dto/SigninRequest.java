package com.example.project.auth.dto;

import com.example.project.enums.LoginProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "로그인 정보")
public class SigninRequest {
    @Schema(description = "유저 비밀 정보")
    private UserSecurityDto userSecurityDto;
}
