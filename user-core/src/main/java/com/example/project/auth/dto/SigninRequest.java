package com.example.project.auth.dto;

import com.example.project.enums.LoginProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequest {
    private String email;
    private String password;
    private String socialId;
    private LoginProvider loginProvider;
}
