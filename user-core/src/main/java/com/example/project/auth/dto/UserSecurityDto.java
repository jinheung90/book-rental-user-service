package com.example.project.auth.dto;

import com.example.project.auth.entity.UserSecurity;
import com.example.project.enums.LoginProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;


@Getter
@AllArgsConstructor
@Builder
public class UserSecurityDto {

    private String email;
    private String password;
    private LoginProvider provider;
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
