package com.example.project.user.service;


import ch.qos.logback.core.util.StringUtil;
import com.example.project.common.enums.BookSellType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    @Value("${token.refresh-token-expired}")
    private Long refreshTokenExpiredSec;

    private final RedisTemplate<String, Long> stringLongTemplate;
    private static final String REFRESH_TOKEN_KEY = "refresh-token-key:";

    public String setRefreshTokenKey(Long id) {
        String refreshToken = UUID.randomUUID().toString();
        stringLongTemplate.opsForValue().set(REFRESH_TOKEN_KEY + refreshToken, id, Duration.ofSeconds(refreshTokenExpiredSec));
        return refreshToken;
    }

    public boolean matchRefreshToken(String refreshToken, Long id) {

        if(refreshToken == null || refreshToken.isBlank()) {
            return false;
        }

        ValueOperations<String, Long> valueOperations = stringLongTemplate.opsForValue();
        Long storedId = valueOperations.get(REFRESH_TOKEN_KEY + refreshToken);

        if(!Objects.equals(storedId, id)) {
            log.warn("refresh token's id and jwt id is not match");
            return false;
        }

        return true;
    }
}
