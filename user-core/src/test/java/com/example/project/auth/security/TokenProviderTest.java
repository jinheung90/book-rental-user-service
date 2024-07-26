package com.example.project.auth.security;

import com.example.project.AuthCoreApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

@SpringBootTest(classes = AuthCoreApplication.class)
@Tag("integration")
@ActiveProfiles("test")
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void create_test_token() {
        final String token = tokenProvider.createJwtAccessTokenByUser(List.of("ROLE_ADMIN"), 1L);
        Assertions.assertFalse(token.isBlank(), token);

    }
}
