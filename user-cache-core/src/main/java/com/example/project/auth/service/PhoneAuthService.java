package com.example.project.auth.service;

import com.example.project.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.util.CommonFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class PhoneAuthService {

    private final RedisTemplate<String,String> redisTemplateForString;

    private static final String PHONE_AUTH_KEY = "auth-phone:";
    private static final String PHONE_AUTH_TEMP_KEY = "auth-phone-temp:";
    public String setPhoneAuthNumber(String phone) {
        String authNumber = String.valueOf(CommonFunction.getRandomNumber6Digit());
        redisTemplateForString.opsForValue().set(
                PHONE_AUTH_KEY + phone,
                authNumber,
                Duration.ofMinutes(3));
        return authNumber;
    }

    public String getPhoneAuthNumber(String phone) {
        return redisTemplateForString.opsForValue().get(PHONE_AUTH_KEY + phone);
    }

    public void setPhoneAuthTempToken(String phone) {
        String tempToken = CommonFunction.generateUpperLettersAndNum(10);
        redisTemplateForString.opsForValue().set(PHONE_AUTH_TEMP_KEY + phone, tempToken);
    }

    public void matchPhoneAuthTempToken(String phone, String token) {
        String tempToken = redisTemplateForString.opsForValue().get(PHONE_AUTH_TEMP_KEY + phone);
        if(!token.equals(tempToken)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "phone auth error");
        }
    }
}
