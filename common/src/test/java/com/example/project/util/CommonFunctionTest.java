package com.example.project.util;

import com.example.project.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
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
}
