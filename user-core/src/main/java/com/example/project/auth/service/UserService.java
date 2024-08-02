package com.example.project.auth.service;


import com.example.project.auth.dto.SigninRequest;
import com.example.project.auth.dto.UserDto;
import com.example.project.auth.dto.UserProfileDto;
import com.example.project.auth.dto.UserSecurityDto;
import com.example.project.auth.entity.Authority;
import com.example.project.auth.entity.User;

import com.example.project.auth.entity.UserSecurity;
import com.example.project.aws.s3.BucketType;
import com.example.project.aws.s3.S3Uploader;
import com.example.project.enums.LoginProvider;
import com.example.project.auth.repository.UserRepository;
import com.example.project.auth.repository.UserSecurityRepository;
import com.example.project.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.util.CommonFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserSecurityRepository userSecurityRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    public Optional<UserSecurity> findUserBySocialLogin(String socialId, LoginProvider loginProvider) {
        return userSecurityRepository.findBySocialMemberIdAndProvider(socialId, loginProvider);
    }

    public boolean existsUserByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public UserSecurity signupByEmail(UserDto userDto, MultipartFile file, UserSecurityDto userSecurityDto, UserProfileDto userProfileDto, String phoneNumber) {
        this.verifyPassword(userSecurityDto.getPassword());
        this.verifyEmail(userSecurityDto.getEmail());
        final User user = this.saveUser(userDto,userProfileDto, file);
        return this.saveUserSecurityWithEmail(user, userSecurityDto.getPassword());
    }

    public UserSecurity signin(SigninRequest signinRequest) {
        final UserSecurityDto userSecurityDto = signinRequest.getUserSecurityDto();
        final LoginProvider loginProvider = userSecurityDto.getProvider();
        if(loginProvider.equals(LoginProvider.EMAIL)) {
            return this.signinByEmail(userSecurityDto.getEmail(), userSecurityDto.getPassword());
        } else if(loginProvider.equals(LoginProvider.KAKAO)) {
            return this.signinByKakao(userSecurityDto.getSocialId());
        } else {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.NOT_SUPPORT_LOGIN_PROVIDER);
        }
    }

    public UserSecurity signinByEmail(String email, String password) {
        final UserSecurity userSecurity = this.userSecurityRepository.findByEmailAndProvider(email, LoginProvider.EMAIL)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER));
        this.matchPassword(password, userSecurity.getPassword());
        return userSecurity;
    }

    public UserSecurity signinByKakao(String socialId) {
        final UserSecurity userSecurity = this.userSecurityRepository.findBySocialMemberIdAndProvider(socialId, LoginProvider.KAKAO)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER));
        return userSecurity;
    }



    public void matchPassword(String password, String encodedPassword) {
        if(!this.passwordEncoder.matches(password, encodedPassword)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER);
        }
    }

    @Transactional
    public UserSecurity signup(UserDto userDto, MultipartFile file, UserSecurityDto userSecurityDto, UserProfileDto userProfileDto, String phoneNumber) {
        if(userSecurityDto.getProvider().equals(LoginProvider.EMAIL)) {
            return this.signupByEmail(userDto, file, userSecurityDto,userProfileDto, phoneNumber);
        }
        return this.signupBySocial(userDto, file, userSecurityDto, userProfileDto, phoneNumber);
    }

    public UserSecurity signupBySocial(UserDto userDto, MultipartFile file, UserSecurityDto userSecurityDto, UserProfileDto userProfileDto, String phoneNumber) {
        this.findUserBySocialLogin(userSecurityDto.getSocialId(), userSecurityDto.getProvider())
                .ifPresent(((userSecurity) -> {
                    throw new RuntimeExceptionWithCode(GlobalErrorCode.EXISTS_USER);
                }));
        final User user = this.saveUser(userDto,userProfileDto, file);
        return this.saveUserSecurityWithSocial(user, userSecurityDto);
    }


    public void verifyEmail(String email) {
        CommonFunction.matchEmailRegex(email);
    }

    public void verifyPassword(String password) {
        CommonFunction.matchPasswordRegex(password);
    }

    public User saveUser(UserDto userDto, UserProfileDto userProfileDto, MultipartFile profileImage) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_USER"));
        User user = User.builder()
                .authorities(authorities)
                .email(userDto.getEmail())
                .nickName(userProfileDto.getNickName())
                .build();
        user = userRepository.save(user);
        String url = this.uploadProfileImage(profileImage, user.getId());
        user.updateProfileImageUrl(url);
        return user;
    }

    public String uploadProfileImage(MultipartFile multipartFile, Long userId) {
        if(multipartFile.isEmpty()) {
           return "";
        }
        try {
            return s3Uploader.putImage(multipartFile, BucketType.USER, userId.toString());
        } catch (Exception e) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.S3_IMAGE_UPLOAD_ERROR);
        }
    }

    public UserSecurity saveUserSecurityWithSocial(User user, UserSecurityDto userSecurityDto) {
        final UserSecurity userSecurity = UserSecurity.builder()
                .user(user)
                .email(userSecurityDto.getEmail())
                .password("empty")
                .provider(userSecurityDto.getProvider())
                .socialMemberId(userSecurityDto.getSocialId())
                .build();
         return userSecurityRepository.save(userSecurity);
    }

    public UserSecurity saveUserSecurityWithEmail(User user, String password) {
        final UserSecurity userSecurity = UserSecurity.builder()
                .user(user)
                .email(user.getEmail())
                .password(passwordEncoder.encode(password))
                .provider(LoginProvider.EMAIL)
                .build();
        return userSecurityRepository.save(userSecurity);
    }

}
