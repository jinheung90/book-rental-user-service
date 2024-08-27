package com.example.project.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailSignupRequest {
    private EmailSignInRequest emailSignInRequest;
    private UserProfileDto userProfileDto;
    private PhoneDto phoneDto;
}
