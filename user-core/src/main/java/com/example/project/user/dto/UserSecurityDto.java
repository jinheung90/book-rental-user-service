package com.example.project.user.dto;

import com.example.project.user.entity.UserSecurity;
import com.example.project.common.enums.LoginProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;


@Getter
@AllArgsConstructor
@Builder
@Schema(description = "유저의 비밀 정보 (비밀번호 등등)")
public class UserSecurityDto {
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "비밀번호")
    private String password;
    @Schema(description = "회원가입, 로그인 제공 서비스 타입 EMAIL만 있음")
    private LoginProvider provider;
    @Schema(description = "소셜 id 해당 소셜 서비스의 가입 아이디")
    private String socialId;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserSecurityDto fromEntityWithoutPassword(UserSecurity userSecurity) {
        return UserSecurityDto.builder()
                .email(userSecurity.getEmail())
                .password("")
                .provider(userSecurity.getProvider())
                .socialId(userSecurity.getSocialMemberId())
                .createdAt(userSecurity.getCreatedAt())
                .updatedAt(userSecurity.getUpdatedAt())
                .build();
    }
}
