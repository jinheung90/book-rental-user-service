package com.example.project.user.dto;

import com.example.project.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "유저 정보")
public class UserDto {

    @Schema(description = "유저 아이디")
    private Long id;
    @Schema(description = "이메일")
    @NotEmpty
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
