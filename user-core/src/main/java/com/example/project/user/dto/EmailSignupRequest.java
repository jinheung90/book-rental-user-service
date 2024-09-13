package com.example.project.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//@Getter
//@AllArgsConstructor
//@NoArgsConstructor
//public class EmailSignupRequest {
//    private EmailSignInRequest emailSignInRequest;
//    private UserProfileDto userProfileDto;
//    private PhoneDto phoneDto;
//}

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailSignupRequest {
    private String email;
    private String password;
    private String profileImageUrl;
    private String nickName;
    private List<UserAddressDto> addresses;
    private String phone;
    private String authTempToken;
}