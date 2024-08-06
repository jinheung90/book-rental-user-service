package com.example.project.aws.s3;

import com.example.project.common.aws.s3.BucketType;
import com.example.project.common.aws.s3.S3Uploader;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.web.multipart.MultipartFile;


import java.nio.charset.StandardCharsets;


@SpringBootTest
@Tag("integration")
@ActiveProfiles("local")
class S3UploaderTest {

    @Autowired
    private S3Uploader s3Uploader;

    private MultipartFile mockFile;

    @Test
    void uploadS3BucketTest() throws Exception {
        String testData = "abcd";
        mockFile = new MockMultipartFile("test5", "test5.txt", "text/plain", testData.getBytes(StandardCharsets.UTF_8));
        s3Uploader.putImage(mockFile, BucketType.BOOK);
    }
}
