package com.example.project.auth.dto;

import com.example.project.auth.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
