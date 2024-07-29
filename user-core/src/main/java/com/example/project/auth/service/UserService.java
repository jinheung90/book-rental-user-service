package com.example.project.auth.service;


import com.example.project.auth.dto.UserDto;
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

    public UserSecurity signupByEmail(UserDto userDto, MultipartFile file, UserSecurityDto userSecurityDto) {
        this.verifyPassword(userSecurityDto.getPassword());
        this.verifyEmail(userSecurityDto.getEmail());
        final User user = this.saveUser(userDto, file);
        return this.saveUserSecurityWithEmail(user, userSecurityDto.getPassword());
    }

    @Transactional
    public UserSecurity signup(UserDto userDto, MultipartFile file, UserSecurityDto userSecurityDto) {
        if(userSecurityDto.getProvider().equals(LoginProvider.EMAIL)) {
            return this.signupByEmail(userDto, file, userSecurityDto);
        }
        return this.signupBySocial(userDto, file, userSecurityDto);
    }

    public UserSecurity signupBySocial(UserDto userDto, MultipartFile file, UserSecurityDto userSecurityDto) {
        this.findUserBySocialLogin(userSecurityDto.getSocialId(), userSecurityDto.getProvider())
                .ifPresent(((userSecurity) -> {
                    throw new RuntimeExceptionWithCode(GlobalErrorCode.EXISTS_USER);
                }));
        final User user = this.saveUser(userDto, file);
        return this.saveUserSecurityWithSocial(user, userSecurityDto);
    }


    public void verifyEmail(String email) {
        CommonFunction.matchEmailRegex(email);
    }

    public void verifyPassword(String password) {
        CommonFunction.matchPasswordRegex(password);
    }

    public User saveUser(UserDto userDto, MultipartFile profileImage) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_USER"));
        User user = User.builder()
                .authorities(authorities)
                .email(userDto.getEmail())
                .address(userDto.getAddress())
                .nickName(userDto.getNickName())
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
