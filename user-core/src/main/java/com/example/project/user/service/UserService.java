package com.example.project.user.service;


import com.example.project.user.dto.UserProfileDto;
import com.example.project.user.dto.UserSecurityDto;
import com.example.project.user.entity.Authority;
import com.example.project.user.entity.User;

import com.example.project.user.entity.UserSecurity;
import com.example.project.common.aws.s3.BucketType;
import com.example.project.common.aws.s3.S3Uploader;
import com.example.project.common.enums.LoginProvider;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.repository.UserSecurityRepository;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.common.util.CommonFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional
    public UserSecurity signupByEmail(MultipartFile file, UserSecurityDto userSecurityDto, UserProfileDto userProfileDto, String phoneNumber) {
        this.verifyPassword(userSecurityDto.getPassword());
        this.verifyEmail(userSecurityDto.getEmail());
        final User user = this.saveUser(userSecurityDto, userProfileDto, file, phoneNumber);
        return this.saveUserSecurityWithEmail(user, userSecurityDto.getPassword());
    }

    @Transactional
    public UserSecurity signinByEmail(String email, String password) {
        final UserSecurity userSecurity = this.userSecurityRepository.findByEmailAndProvider(email, LoginProvider.EMAIL)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER));
        this.matchPassword(password, userSecurity.getPassword());
        return userSecurity;
    }

    public UserSecurity signinByKakao(String socialId) {
        return this.userSecurityRepository.findBySocialMemberIdAndProvider(socialId, LoginProvider.KAKAO)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER));
    }

    public void matchPassword(String password, String encodedPassword) {
        if(!this.passwordEncoder.matches(password, encodedPassword)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER);
        }
    }

    public UserSecurity signupBySocial(MultipartFile file, UserSecurityDto userSecurityDto, UserProfileDto userProfileDto, String phoneNumber) {
        this.findUserBySocialLogin(userSecurityDto.getSocialId(), userSecurityDto.getProvider())
                .ifPresent(((userSecurity) -> {
                    throw new RuntimeExceptionWithCode(GlobalErrorCode.EXISTS_USER);
                }));
        final User user = this.saveUser(userSecurityDto, userProfileDto, file, phoneNumber);
        return this.saveUserSecurityWithSocial(user, userSecurityDto);
    }

    public void verifyEmail(String email) {
        CommonFunction.matchEmailRegex(email);
    }

    public void verifyPassword(String password) {
        CommonFunction.matchPasswordRegex(password);
    }

    public User saveUser(UserSecurityDto userSecurityDto, UserProfileDto userProfileDto, MultipartFile profileImage, String phoneNumber) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_USER"));
        User user = User.builder()
                .authorities(authorities)
                .email(userSecurityDto.getEmail())
                .nickName(userProfileDto.getNickName())
                .phone(phoneNumber)
                .build();
        user = userRepository.save(user);

        if(profileImage != null && !profileImage.isEmpty()) {
            String url = this.uploadProfileImage(profileImage, user.getId());
            user.updateProfileImageUrl(url);
        }

        return user;
    }

    public String uploadProfileImage(MultipartFile multipartFile, Long userId) {

        return s3Uploader.putImage(multipartFile, BucketType.USER, userId.toString());
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

    public Map<Long, UserProfileDto> getUserProfilesByUserIds(List<Long> ids) {
        return userRepository.findByIdIn(ids)
                .stream().map(UserProfileDto::fromEntity)
                .collect(Collectors.toMap(UserProfileDto::getId, userProfileDto -> userProfileDto));
    }


}
