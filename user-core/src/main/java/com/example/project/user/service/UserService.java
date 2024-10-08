package com.example.project.user.service;

import com.example.project.address.dto.KakaoAddressSearchDto;
import com.example.project.user.client.dto.KakaoProfile;
import com.example.project.user.dto.EmailSignupRequest;
import com.example.project.user.dto.KakaoSignupRequest;
import com.example.project.user.dto.UserProfileDto;
import com.example.project.user.entity.*;

import com.example.project.common.aws.s3.BucketType;
import com.example.project.common.aws.s3.S3Uploader;
import com.example.project.common.enums.LoginProvider;
import com.example.project.user.repository.UserAddressRepository;
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
    private final UserAddressRepository userAddressRepository;

    public Optional<UserSecurity> findUserBySocialLogin(String socialId, LoginProvider loginProvider) {
        return userSecurityRepository.findBySocialMemberIdAndProvider(socialId, loginProvider);
    }

    @Transactional(readOnly = true)
    public boolean existsUserByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Transactional(readOnly = true)
    public User findUserByPhone(String phone) {
        return userRepository.findOneWithAuthoritiesAndUserSecuritiesByPhone(phone)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER, " not exists user from phone"));
    }

    @Transactional(readOnly = true)
    public UserSecurity findUserSecurityByPhoneAndEmailAndLoginProvider(String phone, LoginProvider loginProvider) {
        return userSecurityRepository.findByUserPhoneAndProvider(phone, loginProvider)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER, " not exists user from phone and email"));
    }

    @Transactional
    public UserSecurity passwordReset(String phone, String password) {
        CommonFunction.matchPasswordRegex(password);
        final UserSecurity userSecurity = findUserSecurityByPhoneAndEmailAndLoginProvider(phone, LoginProvider.EMAIL);
        userSecurity.setPassword(passwordEncoder.encode(password));
        return userSecurity;
    }

    public void duplicatedEmail(String email) {
        this.verifyEmail(email);
        if(userRepository.existsByEmail(email)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.EXISTS_USER, "이메일 중복");
        }
    }

    @Transactional
    public UserSecurity signupByEmail(EmailSignupRequest request) {
        this.verifyPassword(request.getPassword());
        this.duplicatedEmail(request.getEmail());
        this.duplicatedNickname(request.getNickName());
        final User user = this.saveUser(request.getEmail(), request.getPhone());
        final UserProfile userProfile = this.saveUserProfile(request.getNickName(), request.getProfileImageUrl(), user);
        user.setUserProfile(userProfile);
        return this.saveUserSecurityWithEmail(user, request.getPassword());
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
        userSecurity.getUser().getUserProfile().eraseNickNameForUserInactive();
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

    @Transactional
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

    @Transactional
    public UserSecurity signupByKakao(KakaoSignupRequest kakaoSignupRequest, KakaoProfile kakaoProfile) {
        this.duplicatedNickname(kakaoSignupRequest.getNickName());
        this.findUserBySocialLogin(kakaoProfile.getId().toString(), LoginProvider.KAKAO)
                .ifPresent(((userSecurity) -> {
                    throw new RuntimeExceptionWithCode(GlobalErrorCode.EXISTS_USER);
                }));
        final User user = this.saveUser(kakaoSignupRequest.getEmail(), kakaoSignupRequest.getPhone());
        final UserProfile userProfile = this.saveUserProfile(kakaoSignupRequest.getNickName(), kakaoSignupRequest.getProfileImageUrl(), user);
        user.setUserProfile(userProfile);
        return this.saveUserSecurityWithSocial(user, kakaoProfile.getKakao_account().getEmail(), kakaoProfile.getId().toString(), LoginProvider.KAKAO);
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

    @Transactional
    public UserProfile saveUserProfile(String nickname, String imageUrl, User user) {

        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .nickName(nickname)
                .profileImageUrl(imageUrl)
                .build();

        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public UserProfile updateUserProfile(UserProfileDto userProfileDto, Long userId) {

        final UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.NOT_EXISTS_USER));

        if(!StringUtils.isNullOrEmpty(userProfileDto.getNickName())) {
            this.duplicatedNicknameNotMe(userProfileDto.getNickName(), userId);
            userProfile.setNickName(userProfileDto.getNickName());
        }

        userProfile.updateProfileImageUrl(userProfileDto.getProfileImageUrl());

        return userProfile;
    }

    @Transactional
    public List<UserAddress> updateUserAddress(User user, List<KakaoAddressSearchDto.Documents> address) {
        this.deleteAllUserAddressByUserId(user.getId());
        return userAddressRepository.saveAll(
            address.stream().map(documents -> {
                KakaoAddressSearchDto.RoadAddressDto roadAddressDto = documents.getRoad_address();
                return UserAddress.builder()
                    .addressName(roadAddressDto.getAddress_name())
                    .buildingName(roadAddressDto.getBuilding_name())
                    .latitude(Double.valueOf(roadAddressDto.getY()))
                    .longitude(Double.valueOf(roadAddressDto.getX()))
                    .region1depthName(roadAddressDto.getRegion_1depth_name())
                    .region2depthName(roadAddressDto.getRegion_2depth_name())
                    .region3depthName(roadAddressDto.getRegion_3depth_name())
                    .zoneNo(roadAddressDto.getZone_no())
                    .mainBuildingNo(roadAddressDto.getMain_building_no())
                    .undergroundYn(roadAddressDto.getUnderground_yn())
                    .subBuildingNo(roadAddressDto.getSub_building_no())
                    .roadName(roadAddressDto.getRoad_name())
                    .user(user)
                    .build();
            }).toList()
        );
    }

    @Transactional
    public void deleteAllUserAddressByUserId(Long userId) {
        userAddressRepository.deleteAllByUserId(userId);
    }

    public String uploadProfileImage(MultipartFile multipartFile, Long userId) {
        return s3Uploader.putImage(multipartFile, BucketType.USER, userId.toString());
    }

    public UserSecurity saveUserSecurityWithSocial(User user, String socialEmail, String socialId, LoginProvider loginProvider) {
        final UserSecurity userSecurity = UserSecurity.builder()
                .user(user)
                .email(Objects.requireNonNullElse(socialEmail, ""))
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

    @Transactional(readOnly = true)
    public Map<Long, UserProfileDto> getUserProfilesByUserIds(List<Long> ids) {
        return userRepository.findByIdIn(ids)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> UserProfileDto.fromEntityWithoutAddress(user.getUserProfile())));
    }

    @Transactional(readOnly = true)
    public List<User> findUserAll() {
        return userRepository.findAll();
    }

    public String generateS3ImageUrlForProfile() {
        return s3Uploader.createUserProfileImagePreSignedUrl(UUID.randomUUID().toString());
    }

}
