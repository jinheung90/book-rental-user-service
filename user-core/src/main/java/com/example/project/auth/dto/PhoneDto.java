package com.example.project.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "휴대폰 인증 정보")
public class PhoneDto {
    @Schema(description = "휴대폰번호")
    @NotEmpty
    private String phone;
    @Schema(description = "인증 번호")
    private String authNumber;
    @Schema(description = "인증 완료 후 임시토큰")
    private String authTempToken;
}
