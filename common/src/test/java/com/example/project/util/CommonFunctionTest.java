package com.example.project.util;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.util.CommonFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Tag("unit")
class CommonFunctionTest {

    @Test
    void emailRegexTest() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(RuntimeExceptionWithCode.class, () ->  CommonFunction.matchEmailRegex("aaaa@bbbbbccccasdf")),
                () -> Assertions.assertDoesNotThrow(() -> CommonFunction.matchEmailRegex("jinheung90@gmail.com"))
        );
    }

    @Test
    void passwordRegexTest() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(RuntimeExceptionWithCode.class, () ->  CommonFunction.matchPasswordRegex("aaaa@bbbbbasdf")),
                () -> Assertions.assertThrows(RuntimeExceptionWithCode.class, () ->  CommonFunction.matchPasswordRegex("aaaa12bbbbccasdf")),
                () -> Assertions.assertDoesNotThrow(() -> CommonFunction.matchPasswordRegex("as12gdv4!!"))
        );
    }

    @Test
    void getNumberTest() {
        for (int i = 0; i < 100; i++) {
            final int value = CommonFunction.getRandomNumber6Digit();
            Assertions.assertFalse(value < 100000 || value > 999999);
        }
    }

    @Test
    void phoneRegexTest() {
        Assertions.assertDoesNotThrow(() -> CommonFunction.matchPhoneRegex("01031230851"));
        Assertions.assertThrows(RuntimeExceptionWithCode.class, () -> CommonFunction.matchPhoneRegex("1231245"));
    }
}
