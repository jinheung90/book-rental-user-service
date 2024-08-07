package com.example.project.user.service;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.common.util.CommonFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class PhoneAuthService {

    private final RedisTemplate<String,String> stringRedisTemplate;

    private static final String PHONE_AUTH_KEY = "auth-phone:";
    private static final String PHONE_AUTH_TEMP_KEY = "auth-phone-temp:";
    public String setPhoneAuthNumber(String phone) {
        String authNumber = String.valueOf(CommonFunction.getRandomNumber6Digit());
        stringRedisTemplate.opsForValue().set(
                PHONE_AUTH_KEY + phone,
                authNumber,
                Duration.ofMinutes(3));
        return authNumber;
    }

    public String getPhoneAuthNumber(String phone) {
        return stringRedisTemplate.opsForValue().get(PHONE_AUTH_KEY + phone);
    }

    public String setPhoneAuthTempToken(String phone) {
        String tempToken = CommonFunction.generateUpperLettersAndNum(10);
        stringRedisTemplate.opsForValue().set(PHONE_AUTH_TEMP_KEY + phone, tempToken);
        return tempToken;
    }

    public void matchPhoneAuthTempToken(String phone, String token) {
        String tempToken = stringRedisTemplate.opsForValue().get(PHONE_AUTH_TEMP_KEY + phone);
        if(!token.equals(tempToken)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "phone auth error");
        }
    }
}
