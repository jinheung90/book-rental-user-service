package com.example.project.auth.api;


import com.example.project.auth.security.TokenProvider;
import com.example.project.clients.dto.KakaoProfile;
import com.example.project.clients.dto.KakaoToken;
import com.example.project.auth.dto.SocialLoginResponse;
import com.example.project.auth.entity.User;
import com.example.project.auth.entity.UserSecurity;
import com.example.project.enums.LoginProvider;
import com.example.project.auth.security.CustomAuthenticationProvider;
import com.example.project.auth.service.UserService;
import com.example.project.clients.api.KakaoApiClient;
import com.example.project.auth.dto.KakaoLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final KakaoApiClient kakaoApiClient;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    @PostMapping("/login/kakao")
    public ResponseEntity<SocialLoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
        KakaoToken kakaoToken = kakaoApiClient.getKakaoTokenFromAuthorizationCode(kakaoLoginRequest.getAuthorizationCode());
        KakaoProfile kakaoProfile = kakaoApiClient.fetchUserProfile(kakaoToken.getAccess_token());
        Optional<UserSecurity> optionalUserSecurity = userService.findUserBySocialLogin(kakaoProfile.getId().toString(), LoginProvider.kakao);
        UserSecurity userSecurity = null;

        if(optionalUserSecurity.isEmpty()) {
            // signup

        } else {
            // signin
            userSecurity = optionalUserSecurity.get();
        }

        User user = userSecurity.getUser();
        customAuthenticationProvider.setAuthentication(user.getId(), userSecurity.getEmail(), user.getAuthorityNames());
        String accessToken = tokenProvider.createJwtAccessTokenByUser(user.getAuthorityNames(), user.getId());
        SocialLoginResponse response = SocialLoginResponse.from(accessToken, LoginProvider.kakao, userSecurity.getEmail(), userSecurity.getSocialMemberId(), user.getId());
        return ResponseEntity.ok().body(response);
    }
}
