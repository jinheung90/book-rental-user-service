package com.example.project.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {
    @Schema(description = "패스워드")
    private String password;
    @NotEmpty
    private String phone;
    @Schema(description = "인증 완료 후 임시토큰")
    private String authTempToken;
}
