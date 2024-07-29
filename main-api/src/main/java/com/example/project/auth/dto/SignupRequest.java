package com.example.project.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private UserDto userDto;
    private UserSecurityDto userSecurityDto;
}
