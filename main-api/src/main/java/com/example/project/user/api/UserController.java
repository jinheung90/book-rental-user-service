package com.example.project.user.api;


import com.example.project.book.service.BookService;
import com.example.project.common.aws.sns.SnsSender;

import com.example.project.common.util.CommonFunction;
import com.example.project.common.util.ResponseBody;
import com.example.project.user.client.api.KakaoAuthApiClient;
import com.example.project.user.client.dto.KakaoProfile;
import com.example.project.user.client.dto.KakaoToken;
import com.example.project.user.dto.LoginResponse;
import com.example.project.user.dto.*;

import com.example.project.user.entity.User;
import com.example.project.user.entity.UserProfile;
import com.example.project.user.entity.UserSecurity;
import com.example.project.user.enums.PhoneAuthKeys;
import com.example.project.user.security.CustomUserDetail;
import com.example.project.user.security.TokenProvider;
import com.example.project.user.service.PhoneAuthService;
import com.example.project.user.service.UserService;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final PhoneAuthService phoneAuthService;
    private final TokenProvider tokenProvider;
    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final BookService bookService;
    private final UserService userService;
    private final SnsSender snsSender;

    @PostMapping(
        value = "/signup/email",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "회원가입")
    public ResponseEntity<LoginResponse> signup(
        @Parameter(description = "프로필 이미지 파일") @RequestPart(name = "file", required = false) MultipartFile multipartFile,
        @Parameter(description = "유저 비밀 정보", required = true) @RequestPart(name = "emailSignInRequest") EmailSignInRequest emailSignInRequest,
        @Parameter(description = "유저 프로필 정보") @RequestPart(name = "userProfileDto") UserProfileDto userProfileDto,
        @Parameter(description = "휴대폰 정보", required = true) @RequestPart(name = "phoneDto") PhoneDto phoneDto
    ) {
        phoneAuthService.matchPhoneAuthTempToken(phoneDto.getPhone(), phoneDto.getAuthTempToken());
        final UserSecurity userSecurity = userService.signupByEmail(multipartFile, emailSignInRequest.getEmail(), emailSignInRequest.getPassword(), userProfileDto, phoneDto.getPhone());
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(user.getAuthorityNames(), user.getId());
        phoneAuthService.delPhoneAuthTempToken(phoneDto.getPhone());
        return ResponseEntity.ok(
            new LoginResponse(
                accessToken,
                new UserAuthorityDto(user.getAuthorityNames()),
                UserProfileDto.fromEntity(user.getUserProfile()),
                UserDto.fromEntity(user)
            )
        );
    }

    @PostMapping(
            value = "/signup/kakao",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "회원가입 카카오")
    public ResponseEntity<LoginResponse> signup(
            @Parameter(description = "프로필 이미지 파일") @RequestPart(name = "file", required = false) MultipartFile multipartFile,
            @Parameter(description = "카카오 정보, 이메일", required = true) @RequestPart(name = "kakaoLoginRequest") KakaoLoginRequest kakaoLoginRequest,
            @Parameter(description = "유저 프로필 정보") @RequestPart(name = "userProfileDto") UserProfileDto userProfileDto,
            @Parameter(description = "휴대폰 정보", required = true) @RequestPart(name = "phoneDto") PhoneDto phoneDto
    ) {
        phoneAuthService.matchPhoneAuthTempToken(phoneDto.getPhone(), phoneDto.getAuthTempToken());
        final KakaoToken kakaoToken = kakaoAuthApiClient.getKakaoTokenFromAuthorizationCode(kakaoLoginRequest.getAuthorizationCode());
        final KakaoProfile kakaoProfile = kakaoAuthApiClient.fetchUserProfile(kakaoToken.getAccess_token());
        final UserSecurity userSecurity = userService.signupByKakao(multipartFile, kakaoLoginRequest.getEmail(), kakaoProfile.getKakao_account().getEmail(),  kakaoProfile.getId().toString(), userProfileDto, phoneDto.getPhone());
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(user.getAuthorityNames(), user.getId());
        phoneAuthService.delPhoneAuthTempToken(phoneDto.getPhone());
        return ResponseEntity.ok(
            new LoginResponse(
                accessToken,
                new UserAuthorityDto(user.getAuthorityNames()),
                UserProfileDto.fromEntity(user.getUserProfile()),
                UserDto.fromEntity(user)
            )
        );
    }

    @PostMapping("/signin/email")
    @Operation(summary = "로그인")
    public ResponseEntity<LoginResponse> signIn(
            @RequestBody EmailSignInRequest emailSignInRequest
    ) {
        final UserSecurity userSecurity = this.userService.signinByEmail(emailSignInRequest.getEmail(), emailSignInRequest.getPassword());
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(userSecurity.getUser().getAuthorityNames(), userSecurity.getUser().getId());
        return ResponseEntity.ok(
            new LoginResponse(
                accessToken,
                new UserAuthorityDto(user.getAuthorityNames()),
                UserProfileDto.fromEntity(user.getUserProfile()),
                UserDto.fromEntity(user)
            )
        );
    }

    @PostMapping("/signin/kakao")
    @Operation(summary = "로그인 카카오")
    public ResponseEntity<LoginResponse> signIn(
            @RequestBody KakaoLoginRequest kakaoLoginRequest
    ) {
        final KakaoToken kakaoToken = kakaoAuthApiClient.getKakaoTokenFromAuthorizationCode(kakaoLoginRequest.getAuthorizationCode());
        final KakaoProfile kakaoProfile = kakaoAuthApiClient.fetchUserProfile(kakaoToken.getAccess_token());
        final UserSecurity userSecurity = this.userService.signinByKakao(kakaoProfile.getId().toString());
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(userSecurity.getUser().getAuthorityNames(), userSecurity.getUser().getId());
        return ResponseEntity.ok(
            new LoginResponse(
                accessToken,
                new UserAuthorityDto(user.getAuthorityNames()),
                UserProfileDto.fromEntity(user.getUserProfile()),
                UserDto.fromEntity(user)
            )
        );
    }

    @PostMapping("/auth/phone/send/signup")
    @Operation(summary = "휴대폰 인증번호 보내기 (회원가입)")
    public ResponseEntity<PhoneDto> sendSnsPhoneAuthNumberWhenSignup(
            @RequestBody PhoneDto phoneDto
    ) {
        CommonFunction.matchPhoneRegex(phoneDto.getPhone());
        if(userService.existsUserByPhone(phoneDto.getPhone())) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "exists phone");
        }
        final String authNumber = phoneAuthService.setPhoneAuthNumber(phoneDto.getPhone(), PhoneAuthKeys.PHONE_AUTH_SIGNUP_KEY);
        this.snsSender.sendPhoneAuthNumberMessage(phoneDto.getPhone(), authNumber);
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping("/auth/phone/send/email")
    @Operation(summary = "휴대폰 인증번호 보내기 (이메일 찾기)")
    public ResponseEntity<PhoneDto> sendSnsPhoneAuthNumberWhenFindEmail(
            @RequestBody PhoneDto phoneDto
    ) {
        CommonFunction.matchPhoneRegex(phoneDto.getPhone());
        final String authNumber = phoneAuthService.setPhoneAuthNumber(phoneDto.getPhone(), PhoneAuthKeys.PHONE_AUTH_EMAIL_KEY);
        this.snsSender.sendPhoneAuthNumberMessage(phoneDto.getPhone(), authNumber);
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping("/auth/phone/send/password")
    @Operation(summary = "휴대폰 인증번호 보내기 (비밀번호 재설정)")
    public ResponseEntity<PhoneDto> sendSnsPhoneAuthNumberWhenPasswordReset(
            @RequestBody PhoneDto phoneDto
    ) {
        CommonFunction.matchPhoneRegex(phoneDto.getPhone());
        final String authNumber = phoneAuthService.setPhoneAuthNumber(phoneDto.getPhone(), PhoneAuthKeys.PHONE_AUTH_PASSWORD_KEY);
        this.snsSender.sendPhoneAuthNumberMessage(phoneDto.getPhone(), authNumber);
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping("/auth/phone/verify/signup")
    @Operation(summary = "휴대폰 인증번호 검증 (회원가입)")
    public ResponseEntity<PhoneDto> verifyPhoneAuthNumberWhenSignup(
            @RequestBody PhoneDto phoneDto
    ) {
        String authNumber = phoneAuthService.getPhoneAuthNumber(phoneDto.getPhone(), PhoneAuthKeys.PHONE_AUTH_SIGNUP_KEY);

        if(authNumber == null) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PHONE_AUTH_NUM_EXPIRED);
        }

        if(!authNumber.equals(phoneDto.getAuthNumber())) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PASSWORD_NOT_MATCH);
        }
        String tempToken = phoneAuthService.setPhoneAuthTempToken(phoneDto.getPhone());
        return ResponseEntity.ok().body(new PhoneDto(phoneDto.getPhone(), "" , tempToken));
    }

    @PostMapping("/auth/phone/verify/email")
    @Operation(summary = "휴대폰 인증번호 검증 (이메일 찾기)")
    public ResponseEntity<UserDto> verifyPhoneAuthNumberWhenFindEmail(
            @RequestBody PhoneDto phoneDto
    ) {
        String authNumber = phoneAuthService.getPhoneAuthNumber(phoneDto.getPhone(), PhoneAuthKeys.PHONE_AUTH_EMAIL_KEY);

        if(authNumber == null) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PHONE_AUTH_NUM_EXPIRED);
        }

        if(!authNumber.equals(phoneDto.getAuthNumber())) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PASSWORD_NOT_MATCH);
        }

        final User user = userService.findUserByPhone(phoneDto.getPhone());
        return ResponseEntity.ok().body(UserDto.fromEntity(user));
    }

    @PostMapping("/auth/phone/verify/password")
    @Operation(summary = "휴대폰 인증번호 검증 (비밀번호 재설정)")
    public ResponseEntity<Map<String, Object>> verifyPhoneAuthNumberWhenPasswordReset(
            @RequestBody PasswordResetRequest passwordResetRequest
    ) {
        final PhoneDto phoneDto = passwordResetRequest.getPhoneDto();
        final UserSecurityDto userSecurityDto = passwordResetRequest.getUserSecurityDto();

        String authNumber = phoneAuthService.getPhoneAuthNumber(phoneDto.getPhone(), PhoneAuthKeys.PHONE_AUTH_PASSWORD_KEY);

        if(authNumber == null) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PHONE_AUTH_NUM_EXPIRED);
        }

        if(!authNumber.equals(phoneDto.getAuthNumber())) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PASSWORD_NOT_MATCH);
        }
        log.warn(userSecurityDto.getEmail());
        userService.emailVerifyAndPasswordReset(phoneDto.getPhone(), userSecurityDto.getEmail(), userSecurityDto.getPassword());
        return ResponseEntity.ok().body(ResponseBody.successResponse());
    }

    @PostMapping(value = "/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "회원 정보 수정")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @Parameter(description = "유저 프로필 이미지") @RequestPart(name = "file", required = false) MultipartFile multipartFile,
            @Parameter(description = "유저 프로필 정보") @RequestPart(name = "userProfileDto") UserProfileDto userProfileDto,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        final UserProfile userProfile =
                userService.updateUserProfile(userProfileDto, multipartFile, customUserDetail.getPK());
        return ResponseEntity.ok(UserProfileDto.fromEntity(userProfile));
    }

    @GetMapping(value = "/verify/nickname")
    @Operation(summary = "닉네임 중복 확인", description = "success: true")
    public ResponseEntity<Map<String, Object>> verifyNickname(
            @RequestParam(name = "nickname") String nickname
    ) {
        userService.duplicatedNickname(nickname);
        return ResponseEntity.ok(ResponseBody.successResponse());
    }

    @GetMapping(value = "/verify/email")
    @Operation(summary = "이메일 중복 확인", description = "success: true")
    public ResponseEntity<Map<String, Object>> verifyEmail(
            @RequestParam(name = "email") String email
    ) {
        userService.duplicatedEmail(email);
        return ResponseEntity.ok(ResponseBody.successResponse());
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "success: true")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> withdrawUser(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        userService.withdrawUser(customUserDetail.getPK());
        bookService.inactiveUserBooks(customUserDetail.getPK());

        return ResponseEntity.ok(ResponseBody.successResponse());
    }

    @GetMapping("dummy")
    public ResponseEntity<Map> testDummyData() {
        userService.dummyDataInsert();
        return ResponseEntity.ok(ResponseBody.successResponse());
    }

}
