package com.example.project.auth.api;


import com.example.project.auth.dto.SignupRequest;
import com.example.project.auth.dto.UserDto;
import com.example.project.auth.dto.UserSecurityDto;
import com.example.project.auth.entity.UserSecurity;
import com.example.project.auth.security.TokenProvider;
import com.example.project.auth.dto.LoginResponse;
import com.example.project.auth.security.CustomAuthenticationProvider;
import com.example.project.auth.service.UserService;
import com.example.project.clients.api.KakaoApiClient;
import com.example.project.enums.LoginProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final KakaoApiClient kakaoApiClient;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final CustomAuthenticationProvider customAuthenticationProvider;

//    @PostMapping("/signup")
//    public ResponseEntity<LoginResponse> signup(
//            @RequestPart(value = "file") MultipartFile file,
//            @RequestPart(value = "userDto") UserDto userDto,
//            @RequestPart(value = "useSecurityDto") UserSecurityDto userSecurityDto
//    ) {
//        final UserSecurity userSecurity = userService.signup(userDto, file, userSecurityDto);
//        return ResponseEntity.ok().body(response);
//    }
//

}
