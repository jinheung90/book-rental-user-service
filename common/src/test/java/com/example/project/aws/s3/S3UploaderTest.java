package com.example.project.aws.s3;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

@SpringBootTest
@Tag("integration")
@ActiveProfiles("local")
class S3UploaderTest {

    @Autowired
    private S3Uploader s3Uploader;

    @Test
    void createS3BucketTest() {
        final String realName = s3Uploader.getBucketRealName(BucketType.BOOK);
        s3Uploader.createBucket(realName);
        Assert.isTrue(s3Uploader.existsBucket(realName), "not created");
    }
}
