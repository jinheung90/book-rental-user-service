package com.example.project.user.service;


import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.user.dto.UserProfileDto;
import com.example.project.user.entity.UserProfile;
import com.example.project.user.entity.UserSecurity;
import com.example.project.user.repository.UserProfileRepository;
import com.example.project.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void duplicatedNickNameTest() {
        BDDMockito.given(userProfileRepository.existsByNickNameAndUserIdNot("나는야책마왕", 1L)).willReturn(true);
        Assertions.assertThrows(RuntimeExceptionWithCode.class, () -> userService.duplicatedNicknameNotMe("나는야책마왕", 2L));
    }

    @Test
    void notDuplicatedNickNameTest() {
        BDDMockito.given(userProfileRepository.existsByNickNameAndUserIdNot("가나다", 1L)).willReturn(false);
        Assertions.assertTrue(userService.duplicatedNicknameNotMe("가나다", 1L));
    }

    @Test
    void notMatchPasswordTest() {
        String inputPassword = "가나다라마바사";
        String encoded = passwordEncoder.encode(inputPassword);
        Assertions.assertTrue(passwordEncoder.matches(inputPassword, encoded));
    }
    @Test
    void updateUserProfileTest() {
        final UserProfile testFixture = UserProfile.builder()
                .nickName("b")
                .profileImageUrl("c")
                .build();

        BDDMockito.given(userProfileRepository.findByUserId(2L)).willThrow(RuntimeExceptionWithCode.class);
        final UserProfileDto userProfileDto = UserProfileDto.fromEntity(testFixture);
        Assertions.assertThrows(
                RuntimeExceptionWithCode.class,
                () -> userService.updateUserProfile(userProfileDto, null)
        );
    }

    @Test
    void signInByEmailTest() {

    }
}
