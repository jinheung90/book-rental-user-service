package com.example.project.auth.dto;

import com.example.project.enums.LoginProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.Provider;


@Getter
@AllArgsConstructor
@Builder
public class UserSecurityDto {
    private String email;
    private String password;
    private LoginProvider provider;
    private String socialId;
}
