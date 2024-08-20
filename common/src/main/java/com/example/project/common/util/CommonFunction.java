package com.example.project.common.util;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;

import java.util.Random;

public class CommonFunction {
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String PASSWORD_REGEX = "(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$";

    private static final String PHONE_REGEX = "(01[016789]{1})-?[0-9]{3,4}-?[0-9]{4}$";

    private static final Random random = new Random();

    public static void matchPasswordRegex(String password) {
        if(!password.matches(PASSWORD_REGEX)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.PASSWORD_NOT_MATCH, password);
        }
    }

    public static void matchEmailRegex(String email) {
        if(!email.matches(EMAIL_REGEX)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.EMAIL_REGEX_NOT_MATCH);
        }
    }

    public static void matchPhoneRegex(String phoneNumber) {
        if(!phoneNumber.matches(PHONE_REGEX)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST);
        }
    }

    public static int getRandomNumber6Digit() {
        return random.nextInt(100000, 999999);
    }

    public static String generateUpperLettersAndNum(int size) {
        final int leftLimit = 48; // numeral '0'
        final int rightLimit = 90; // letter 'z'
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> ((i >= 48 && i <= 57) || (i >= 65 && i <= 90))) // 10 이후의 특수문자 제거 문자 사이의 특수문자 제거
                .limit(size)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
