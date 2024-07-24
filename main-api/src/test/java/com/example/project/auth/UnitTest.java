package com.example.project.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

@Tag("unit")
class UnitTest {
    @Test
    void unitTest() {
        Assertions.assertAll(
                () -> Assert.hasText("abc", "error")
        );
    }
}
