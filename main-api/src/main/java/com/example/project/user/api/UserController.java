package com.example.project.user.api;



import com.example.project.book.store.service.BookService;

import com.example.project.common.aws.sns.SnsSender;

import com.example.project.common.util.CommonFunction;
import com.example.project.common.util.ResponseBody;
import com.example.project.user.client.api.KakaoAddressSearchClient;
import com.example.project.user.client.api.KakaoAuthApiClient;
import com.example.project.address.dto.KakaoAddressSearchDto;
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
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final PhoneAuthService phoneAuthService;
    private final TokenProvider tokenProvider;
    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final KakaoAddressSearchClient kakaoAddressSearchClient;
    private final BookService bookService;
    private final UserService userService;
    private final SnsSender snsSender;

    @PostMapping(value = "/signup/email")
    @Operation(summary = "회원가입 (이메일)")
    public ResponseEntity<LoginResponse> signup(
        @RequestBody EmailSignupRequest emailSignupRequest
    ) {
        phoneAuthService.matchPhoneAuthTempToken(emailSignupRequest.getPhone(), emailSignupRequest.getAuthTempToken());
        final UserSecurity userSecurity = userService.signupByEmail(emailSignupRequest);
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(user.getAuthorityNames(), user.getId());
        phoneAuthService.delPhoneAuthTempToken(emailSignupRequest.getPhone());
        return ResponseEntity.ok(
            new LoginResponse(
                accessToken,
                new UserAuthorityDto(user.getAuthorityNames()),
                UserProfileDto.fromEntity(user.getUserProfile()),
                UserDto.fromEntity(user)
            )
        );
    }

    @PostMapping(value = "/signup/kakao")
    @Operation(summary = "회원가입 카카오")
    public ResponseEntity<LoginResponse> signup(
        @RequestBody KakaoSignupRequest kakaoSignupRequest
    ) {

        phoneAuthService.matchPhoneAuthTempToken(kakaoSignupRequest.getPhone(), kakaoSignupRequest.getAuthTempToken());
        final KakaoToken kakaoToken = kakaoAuthApiClient.getKakaoTokenFromAuthorizationCode(kakaoSignupRequest.getAuthorizationCode());
        final KakaoProfile kakaoProfile = kakaoAuthApiClient.fetchUserProfile(kakaoToken.getAccess_token());
        final UserSecurity userSecurity = userService.signupByKakao(kakaoSignupRequest, kakaoProfile);
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(user.getAuthorityNames(), user.getId());
        phoneAuthService.delPhoneAuthTempToken(kakaoSignupRequest.getPhone());
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
    public ResponseEntity<LoginResponse> signInEmail(
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
    public ResponseEntity<LoginResponse> signInKakao(
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
        final String authNumber = phoneAuthService.setSignupPhoneAuthNumber(phoneDto.getPhone());
        this.snsSender.sendPhoneAuthNumberMessage(phoneDto.getPhone(), authNumber);
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping("/auth/phone/send/email")
    @Operation(summary = "휴대폰 인증번호 보내기 (이메일 찾기)")
    public ResponseEntity<PhoneDto> sendSnsPhoneAuthNumberWhenFindEmail(
            @RequestBody PhoneDto phoneDto
    ) {
        CommonFunction.matchPhoneRegex(phoneDto.getPhone());
        final String authNumber = phoneAuthService.setEmailFindPhoneAuthNumber(phoneDto.getPhone());
        this.snsSender.sendPhoneAuthNumberMessage(phoneDto.getPhone(), authNumber);
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping("/auth/phone/send/password")
    @Operation(summary = "휴대폰 인증번호 보내기 (비밀번호 재설정)")
    public ResponseEntity<PhoneDto> sendSnsPhoneAuthNumberWhenPasswordReset(
            @RequestBody PhoneDto phoneDto
    ) {
        CommonFunction.matchPhoneRegex(phoneDto.getPhone());
        final String authNumber = phoneAuthService.setPasswordResetPhoneAuthNumber(phoneDto.getPhone());
        this.snsSender.sendPhoneAuthNumberMessage(phoneDto.getPhone(), authNumber);
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping("/auth/phone/verify/signup")
    @Operation(summary = "휴대폰 인증번호 검증 (회원가입)")
    public ResponseEntity<PhoneDto> verifyPhoneAuthNumberWhenSignup(
            @RequestBody PhoneDto phoneDto
    ) {
        String authNumber = phoneAuthService.getSignupPhoneAuthNumber(phoneDto.getPhone());

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
        String authNumber = phoneAuthService.getFindEmailPhoneAuthNumber(phoneDto.getPhone());

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
    public ResponseEntity<PhoneDto> verifyPhoneAuthNumberWhenPasswordReset(
            @RequestBody PhoneDto phoneDto
    ) {
        String authNumber = phoneAuthService.getPasswordResetPhoneAuthNumber(phoneDto.getPhone());

        if(authNumber == null) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PHONE_AUTH_NUM_EXPIRED);
        }

        if(!authNumber.equals(phoneDto.getAuthNumber())) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PASSWORD_NOT_MATCH);
        }

        String authTempToken = phoneAuthService.setPasswordChangePhoneAuthTempToken(phoneDto.getPhone());
        phoneDto = new PhoneDto("", "", authTempToken);
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping("/auth/password")
    @Operation(summary = "비밀번호 재설정")
    public ResponseEntity<Map> verifyPhoneAuthNumberWhenPasswordReset(
            @RequestBody PasswordResetRequest passwordResetRequest
    ) {
        phoneAuthService.matchPasswordChangePhoneAuthTempToken(passwordResetRequest.getPhone(), passwordResetRequest.getAuthTempToken());
        userService.passwordReset(passwordResetRequest.getPhone(), passwordResetRequest.getPassword());
        phoneAuthService.delPasswordPhoneAuthTempToken(passwordResetRequest.getPhone());
        return ResponseEntity.ok().body(ResponseBody.successResponse());
    }

    @PutMapping(value = "/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "회원 정보 수정")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @RequestBody UserProfileDto userProfileDto,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        List<UserAddressDto> addresses = userProfileDto.getAddresses();

        final UserProfile userProfile = userService.updateUserProfile(userProfileDto, customUserDetail.getPK());
        List<KakaoAddressSearchDto.Documents> kakaoAddress = new ArrayList<>();
        if(addresses != null && !addresses.isEmpty()) {
            for (UserAddressDto address: addresses
                 ) {
                KakaoAddressSearchDto.Documents documents = kakaoAddressSearchClient.findOneByNameAndZoneNo(address.getAddressName(), address.getZoneNo());
                kakaoAddress.add(documents);
            }
            userService.updateUserAddress(userProfile.getUser(), kakaoAddress);
        }

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

    @GetMapping("/profile/image/url")
    public ResponseEntity<String> getPreSignedUrl() {
        return ResponseEntity.ok(userService.generateS3ImageUrlForProfile());
    }


}
