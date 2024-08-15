package com.example.project.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private UserDto userDto;
    private UserSecurityDto userSecurityDto;
    private PhoneDto phoneDto;
}
