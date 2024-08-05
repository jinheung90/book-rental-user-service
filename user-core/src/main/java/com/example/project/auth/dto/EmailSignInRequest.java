package com.example.project.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "이메일 로그인 정보")
public class EmailSignInRequest {
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "패스워드")
    private String password;
}
