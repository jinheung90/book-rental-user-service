package com.example.project.util;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.util.CommonFunction;
import com.example.project.common.util.JamoSeparate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
@Tag("unit")
public class JamoSeparateTest {
    @Test
    void testEngKorMix() {
        String input = "a가a나a다";
        Assertions.assertEquals("aㄱㅏaㄴㅏaㄷㅏ", JamoSeparate.separate(input));
    }
}
