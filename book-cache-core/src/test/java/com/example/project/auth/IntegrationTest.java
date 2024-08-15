package com.example.project.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
@Tag("integration")
class IntegrationTest {

    @Test
    void test() {
        Assertions.assertAll(
                () -> Assert.hasText("abc", "error")
        );
    }
}
