package com.example.project.user.service;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.common.util.CommonFunction;
import com.example.project.user.enums.PhoneAuthKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class PhoneAuthService {

    private final RedisTemplate<String,String> stringRedisTemplate;

    private static final String PHONE_AUTH_TEMP_KEY = "auth-phone-temp:";
    private static final String PASSWORD_PHONE_AUTH_TEMP_KEY = "password-auth-phone-temp:";
    public String setPhoneAuthNumber(String phone, PhoneAuthKeys key) {
        String authNumber = String.valueOf(CommonFunction.getRandomNumber6Digit());
        stringRedisTemplate.opsForValue().set(
                key.getValue() + phone,
                authNumber,
                Duration.ofMinutes(3));
        return authNumber;
    }

    public String getPhoneAuthNumber(String phone, PhoneAuthKeys key) {
        return stringRedisTemplate.opsForValue().get(key.getValue() + phone);
    }

    public String setPhoneAuthTempToken(String phone, PhoneAuthKeys key) {
        String tempToken = CommonFunction.generateUpperLettersAndNum(10);
        stringRedisTemplate.opsForValue().set(key.getValue() + phone, tempToken);
        return tempToken;
    }

    public String setPasswordChangePhoneAuthTempToken(String phone) {
        String tempToken = CommonFunction.generateUpperLettersAndNum(10);
        stringRedisTemplate.opsForValue().set(PASSWORD_PHONE_AUTH_TEMP_KEY + phone, tempToken);
        return tempToken;
    }

    public void matchPhoneAuthTempToken(String phone, String token) {
        final String key = PHONE_AUTH_TEMP_KEY + phone;
        String tempToken = stringRedisTemplate.opsForValue().get(key);
        if(!token.equals(tempToken)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "phone auth error");
        }
    }

    public void matchPasswordChangePhoneAuthTempToken(String phone, String token) {
        final String key = PASSWORD_PHONE_AUTH_TEMP_KEY + phone;
        String tempToken = stringRedisTemplate.opsForValue().get(key);
        if(!token.equals(tempToken)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "phone auth error");
        }
    }
    public void delPhoneAuthTempToken(String phone) {
        stringRedisTemplate.delete(PHONE_AUTH_TEMP_KEY + phone);
    }

    public void delPasswordPhoneAuthTempToken(String phone) {
        stringRedisTemplate.delete(PASSWORD_PHONE_AUTH_TEMP_KEY + phone);
    }
}
