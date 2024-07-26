package com.example.project.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private Long id;
    private String imageUrl;
    private String name;
    private float starRating;
}
