package com.example.project.user.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@AllArgsConstructor
@Schema(description = "로그인, 회원가입 완료")
@Builder
public class LoginResponse {
    @Schema(description = "jwt 토큰")
    private String accessToken;
    @Schema(description = "유저 아이디")
    private Long id;
    @Schema(description = "이메일")
    @NotEmpty
    private String email;
    protected String profileImageUrl;
    protected String nickName;
    private List<UserAddressDto> addresses;
    private List<String> authorities;
    private Instant createdAt;
    private Instant updatedAt;

    public static LoginResponse fromEntity(String accessToken, UserDto userDto) {
        return LoginResponse.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .profileImageUrl(userDto.getProfileImageUrl())
                .nickName(userDto.getNickName())
                .addresses(Objects.requireNonNullElse(userDto.getAddresses(), new ArrayList<>()))
                .authorities(userDto.getAuthorities())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(userDto.getUpdatedAt())
                .accessToken(accessToken)
                .build();

    }
}
