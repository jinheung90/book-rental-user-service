package com.example.project.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {
    private UserSecurityDto userSecurityDto;
    private PhoneDto phoneDto;
}
