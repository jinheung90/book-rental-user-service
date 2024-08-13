package com.example.project.user.service;


import com.example.project.user.dto.UserProfileDto;
import com.example.project.user.dto.UserSecurityDto;
import com.example.project.user.entity.Authority;
import com.example.project.user.entity.User;

import com.example.project.user.entity.UserProfile;
import com.example.project.user.entity.UserSecurity;
import com.example.project.common.aws.s3.BucketType;
import com.example.project.common.aws.s3.S3Uploader;
import com.example.project.common.enums.LoginProvider;
import com.example.project.user.repository.UserProfileRepository;
import com.example.project.user.repository.UserRepository;
import com.example.project.user.repository.UserSecurityRepository;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.common.util.CommonFunction;
import com.querydsl.core.util.StringUtils;
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
    private final UserProfileRepository userProfileRepository;

    public Optional<UserSecurity> findUserBySocialLogin(String socialId, LoginProvider loginProvider) {
        return userSecurityRepository.findBySocialMemberIdAndProvider(socialId, loginProvider);
    }

    public boolean existsUserByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Transactional
    public UserSecurity signupByEmail(MultipartFile file, String email, String password, UserProfileDto userProfileDto, String phoneNumber) {
        this.verifyPassword(password);
        this.verifyEmail(email);
        this.duplicatedNickname(userProfileDto.getNickName());
        final User user = this.saveUser(email, phoneNumber);
        final UserProfile userProfile = this.saveUserProfile(userProfileDto, file, user);
        user.setUserProfile(userProfile);
        return this.saveUserSecurityWithEmail(user, password);
    }

    @Transactional
    public UserSecurity signinByEmail(String email, String password) {
        final UserSecurity userSecurity = this.userSecurityRepository.findByEmailAndProviderAndUserDeletedIsFalse(email, LoginProvider.EMAIL)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER));
        this.matchPassword(password, userSecurity.getPassword());
        return userSecurity;
    }

    @Transactional
    public void withdrawUserWithMatchPassword(String password, Long userId) {
        final UserSecurity userSecurity = this.userSecurityRepository.findByUserId(userId).orElseThrow(
                () -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER)
        );
        this.matchPassword(password, userSecurity.getPassword());
        userSecurity.getUser().inactive();
    }

    @Transactional
    public void withdrawUser(Long userId) {
        final UserSecurity userSecurity = this.userSecurityRepository.findByUserId(userId).orElseThrow(
                () -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER)
        );
        userSecurity.getUser().inactive();
    }


    public boolean duplicatedNicknameNotMe(String nickname, Long userId) {
        if(userProfileRepository.existsByNickNameAndUserIdNot(nickname, userId)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.EXIST_NICKNAME);
        }
        return true;
    }

    public boolean duplicatedNickname(String nickname) {
        if(userProfileRepository.existsByNickName(nickname)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.EXIST_NICKNAME);
        }
        return true;
    }

    public UserSecurity signinByKakao(String socialId) {
        return this.userSecurityRepository.findBySocialMemberIdAndProvider(socialId, LoginProvider.KAKAO)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER));
    }

    public boolean matchPassword(String password, String encodedPassword) {
        if(!this.passwordEncoder.matches(password, encodedPassword)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER);
        }
        return true;
    }

    public UserSecurity signupBySocial(MultipartFile file, String email, String socialId, LoginProvider loginProvider, UserProfileDto userProfileDto, String phoneNumber) {
        this.duplicatedNickname(userProfileDto.getNickName());
        this.findUserBySocialLogin(socialId,loginProvider)
                .ifPresent(((userSecurity) -> {
                    throw new RuntimeExceptionWithCode(GlobalErrorCode.EXISTS_USER);
                }));
        final User user = this.saveUser(email, phoneNumber);
        final UserProfile userProfile = this.saveUserProfile(userProfileDto, file, user);
        user.setUserProfile(userProfile);
        return this.saveUserSecurityWithSocial(user, socialId, loginProvider);
    }

    public void verifyEmail(String email) {
        CommonFunction.matchEmailRegex(email);
    }

    public void verifyPassword(String password) {
        CommonFunction.matchPasswordRegex(password);
    }

    public User saveUser(String email, String phoneNumber) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_USER"));
        User user = User.builder()
                .authorities(authorities)
                .email(email)
                .phone(phoneNumber)
                .build();
        user = userRepository.save(user);
        return user;
    }

    public UserProfile saveUserProfile(UserProfileDto userProfileDto, MultipartFile profileImage, User user) {

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .address(userProfileDto.getAddress())
                .nickName(userProfileDto.getNickName())
                .profileImageUrl("")
                .build();

        if(profileImage != null && !profileImage.isEmpty()) {
            String url = this.uploadProfileImage(profileImage, user.getId());
            userProfile.updateProfileImageUrl(url);
        }

        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public UserProfile updateUserProfile(UserProfileDto userProfileDto, MultipartFile file, Long userId) {

        final UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER));

        if(!StringUtils.isNullOrEmpty(userProfileDto.getNickName())) {
            this.duplicatedNicknameNotMe(userProfileDto.getNickName(), userId);
            userProfile.setNickName(userProfileDto.getNickName());
        }

        if(file != null && !file.isEmpty()) {
            String url = this.uploadProfileImage(file, userId);
            userProfile.updateProfileImageUrl(url);
        }

        if(!StringUtils.isNullOrEmpty(userProfileDto.getAddress())) {
            userProfile.setAddress(userProfile.getAddress());
        }

        return userProfile;
    }

    public String uploadProfileImage(MultipartFile multipartFile, Long userId) {
        return s3Uploader.putImage(multipartFile, BucketType.USER, userId.toString());
    }

    public UserSecurity saveUserSecurityWithSocial(User user, String socialId, LoginProvider loginProvider) {
        final UserSecurity userSecurity = UserSecurity.builder()
                .user(user)
                .email(user.getEmail())
                .password("empty")
                .provider(loginProvider)
                .socialMemberId(socialId)
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
        return userProfileRepository.findAllByUserIdIn(ids)
                .stream().map(UserProfileDto::fromEntity)
                .collect(Collectors.toMap(UserProfileDto::getId, userProfileDto -> userProfileDto));
    }


}
