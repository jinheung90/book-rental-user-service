package com.example.project.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoSignupRequest {
    private String email;
    private String authorizationCode;
    private String profileImageUrl;
    private String nickName;
    private List<UserAddressDto> addresses;
    private String phone;
    private String authTempToken;
}
