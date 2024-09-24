package com.example.project.user.dto;


import com.example.project.user.entity.User;
import com.example.project.user.entity.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LoginResponseDtoTest {

    @Test
    void responseAddressNullTest() {
        String accessTokenTest = "test";
        UserDto test = UserDto.fromEntity(
                User.builder()
                        .userProfile(
                                UserProfile.builder()
                                        .profileImageUrl("")
                                        .nickName("")
                                        .build()
                        )
                        .authorities(new ArrayList<>())
                        .build()
        );
        Assertions.assertDoesNotThrow(() ->  LoginResponse.fromEntity(accessTokenTest, test));
    }
}
