package com.example.project.auth.api;


import com.example.project.auth.dto.*;
import com.example.project.auth.entity.User;
import com.example.project.auth.entity.UserSecurity;
import com.example.project.auth.security.TokenProvider;
import com.example.project.auth.service.PhoneAuthService;
import com.example.project.auth.service.UserService;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final PhoneAuthService phoneAuthService;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping(
        value = "/signUp/email",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "회원가입")
    public ResponseEntity<LoginResponse> signup(
        @Parameter(description = "프로필 이미지 파일") @RequestPart(name = "file", required = false) MultipartFile multipartFile,
        @Parameter(description = "유저 비밀 정보", required = true) @RequestPart(name = "userSecurityDto") UserSecurityDto userSecurityDto,
        @Parameter(description = "유저 프로필 정보") @RequestPart(name = "userProfileDto") UserProfileDto userProfileDto,
        @Parameter(description = "휴대폰 정보", required = true) @RequestPart(name = "phoneDto") PhoneDto phoneDto
    ) {
        phoneAuthService.matchPhoneAuthTempToken(phoneDto.getPhone(), phoneDto.getAuthTempToken());
        final UserSecurity userSecurity = userService.signupByEmail(multipartFile, userSecurityDto, userProfileDto, phoneDto.getPhone());
        final User user = userSecurity.getUser();
        final String accessToken = tokenProvider.createJwtAccessTokenByUser(user.getAuthorityNames(), user.getId());
        return ResponseEntity.ok(
            new LoginResponse(accessToken, new UserAuthorityDto(user.getAuthorityNames()), UserProfileDto.fromEntity(user), UserDto.fromEntity(user))
        );
    }

    @PostMapping("/signIn/email")
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
                    UserProfileDto.fromEntity(user),
                    UserDto.fromEntity(user)
                )
        );
    }

    @PostMapping("/phone")
    @Operation(summary = "휴대폰 인증번호 보내기")
    public ResponseEntity<PhoneDto> sendSnsPhoneAuthNumber(
            @RequestBody PhoneDto phoneDto
    ) {
        if(userService.existsUserByPhone(phoneDto.getPhone())) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "exists phone");
        }
        phoneAuthService.setPhoneAuthNumber(phoneDto.getPhone());
        return ResponseEntity.ok().body(phoneDto);
    }

    @PostMapping(value = "/test/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "테스트 업로드")
    public ResponseEntity<String> testFileUpload(@RequestPart(name = "file1") MultipartFile file) {
        userService.uploadProfileImage(file, 1L);
        return ResponseEntity.ok().body("a");
    }

    @PostMapping("/phone/verify")
    @Operation(summary = "휴대폰 인증번호 검증")
    public ResponseEntity<PhoneDto> verifyPhoneAuthNumber(
            @RequestBody PhoneDto phoneDto
    ) {
        String authNumber = phoneAuthService.getPhoneAuthNumber(phoneDto.getPhone());

        if(authNumber == null) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PHONE_AUTH_NUM_EXPIRED);
        }

        if(!authNumber.equals(phoneDto.getAuthNumber())) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PASSWORD_NOT_MATCH);
        }
        String tempToken = phoneAuthService.setPhoneAuthTempToken(phoneDto.getPhone());
        return ResponseEntity.ok().body(new PhoneDto(phoneDto.getPhone(), "" , tempToken));
    }

    @GetMapping("/test/token")
    public ResponseEntity<String> testTokenProvider() {
        ArrayList<String> auth = new ArrayList<>();
        auth.add("ROLE_USER");
        return ResponseEntity.ok(tokenProvider.createJwtAccessTokenByUser(auth, 1L));
    }
    @GetMapping("/test/phone")
    public ResponseEntity<String> testPhone(
            @RequestParam(name = "phone") String phone
    ) {
        return ResponseEntity.ok(phoneAuthService.setPhoneAuthNumber(phone));
    }

//    @PutMapping(name = "/profile",
//        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasRole('ROLE_USER')")
//    @Operation(summary = "회원 정보 수정")
//    public ResponseEntity<UserProfileDto> updateUserProfile(
//        @RequestPart(name = "file", required = false) MultipartFile multipartFile,
//        @RequestPart(name = "userProfileDto") UserProfileDto userProfileDto
//    ) {
//        return ResponseEntity.ok(new UserProfileDto("","", ""));
//    }

    @GetMapping("/test/load")
    public ResponseEntity<String> test() {

        long a= 1;
        for (int i = 1; i < 100000; i++) {
            a *= i;
        }

        return ResponseEntity.ok("s");
    }
}
