package com.example.project.auth.api;


import com.example.project.auth.dto.*;
import com.example.project.auth.entity.User;
import com.example.project.auth.entity.UserSecurity;
import com.example.project.auth.security.TokenProvider;
import com.example.project.auth.service.PhoneAuthService;
import com.example.project.auth.service.UserService;
import com.example.project.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.errorHandling.errorEnums.GlobalErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final PhoneAuthService phoneAuthService;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping(
        name = "/signup",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원가입")
    public ResponseEntity<LoginResponse> signup(
        @RequestPart(name = "userDto") UserDto userDto,
        @RequestPart(name = "file", required = false) MultipartFile multipartFile,
        @RequestPart(name = "userSecurityDto") UserSecurityDto userSecurityDto,
        @RequestPart(name = "userProfileDto") UserProfileDto userProfileDto,
        @RequestPart(name = "phoneDto") PhoneDto phoneDto
    ) {
        phoneAuthService.matchPhoneAuthTempToken(phoneDto.getPhone(), phoneDto.getAuthTempToken());
        final UserSecurity userSecurity = userService.signup(userDto,multipartFile,userSecurityDto,userProfileDto,phoneDto.getPhone());
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(user.getAuthorityNames(), user.getId());
        return ResponseEntity.ok(
            new LoginResponse(accessToken, new UserAuthorityDto(user.getAuthorityNames()), UserProfileDto.fromEntity(user), UserDto.fromEntity(user))
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signup(
            @RequestBody SigninRequest signinRequest
    ) {
        final UserSecurity userSecurity = this.userService.signin(signinRequest);
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(userSecurity.getUser().getAuthorityNames(), userSecurity.getUser().getId());
        return ResponseEntity.ok(
                new LoginResponse(
                    accessToken,
                    new UserAuthorityDto(user.getAuthorityNames()),
                    UserProfileDto.fromEntity(user),
                    UserDto.fromEntity(user)
                )
        );
    }

    @PostMapping("/phone")
    public ResponseEntity<PhoneDto> sendSnsPhoneAuthNumber(
            @RequestBody PhoneDto phoneDto
    ) {
        final String authNumber = phoneAuthService.setPhoneAuthNumber(phoneDto.getPhone());
        phoneDto.setAuthNumber(authNumber);
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping("/phone/verify")
    public ResponseEntity<PhoneDto> verifyPhoneAuthNumber(
            @RequestBody PhoneDto phoneDto
    ) {
        String authNumber = phoneAuthService.getPhoneAuthNumber(phoneDto.getPhone());

        if(authNumber.isEmpty()) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PHONE_AUTH_NUM_EXPIRED);
        }

        if(!authNumber.equals(phoneDto.getAuthNumber())) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PASSWORD_NOT_MATCH);
        }

        return ResponseEntity.ok().body(phoneDto);
    }

    @GetMapping("/test/token")
    public ResponseEntity<String> testTokenProvider() {
        ArrayList<String> auth = new ArrayList<>();
        auth.add("ROLE_USER");
        return ResponseEntity.ok(tokenProvider.createJwtAccessTokenByUser(auth, 1L));
    }

    @PutMapping(name = "/profile",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원 정보 수정")
    public ResponseEntity<UserProfileDto> updateUserProfile(
        @RequestPart(name = "file", required = false) MultipartFile multipartFile,
        @RequestPart(name = "userProfileDto") UserProfileDto userProfileDto
    ) {
        return ResponseEntity.ok(new UserProfileDto("","", ""));
    }
}
