package com.example.project.common.aws.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import io.undertow.util.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private AmazonS3Client amazonS3Client;

    @Value("${spring.profiles.active}")
    private String profile;

    private final Environment environment;

    private static final Long PRESIGNED_EXPIRED_SEC = 60 * 30L;
    private static final String USER_BOOK_IMAGE_KEY_PREFIX = "user_book/";
    private static final String USER_PROFILE_IMAGE_KEY_PREFIX = "user_profile/";

    @PostConstruct
    public void init() {
        if(profile.equals("local")) {
            String key = environment.getProperty("spring.cloud.aws.credentials.access-key");
            String value = environment.getProperty("spring.cloud.aws.credentials.secret-key");
            String region = environment.getProperty("spring.cloud.aws.region.static");
            this.amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                    .enablePathStyleAccess()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(key, value)))
                    .build();
            return;
        }

        this.amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .enablePathStyleAccess()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    public String createPresignedUrl(String bucketName, String keyName) {
        try {
            Date expired = Date.from(
                Instant.now().plusSeconds(PRESIGNED_EXPIRED_SEC)
                    .atZone(ZoneId.of("Asia/Seoul")).toInstant()
            );
            return this.amazonS3Client.generatePresignedUrl(bucketName, keyName, expired, HttpMethod.PUT).toString();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeExceptionWithCode(GlobalErrorCode.S3_IMAGE_UPLOAD_ERROR, "can't generate url");
        }
    }

    public String createUserBookImagePreSignedUrl(String keyName) {
        return this.createPresignedUrl(getBucketRealName(BucketType.BOOK), USER_BOOK_IMAGE_KEY_PREFIX + keyName);
    }

    public String createUserProfileImagePreSignedUrl(String keyName) {
        return this.createPresignedUrl(getBucketRealName(BucketType.BOOK), USER_BOOK_IMAGE_KEY_PREFIX + keyName);
    }

    public String getBucketRealName(BucketType bucketType) {
        return profile + '-' + bucketType.getName();
    }

    public void deleteS3ByKey(String key, BucketType bucket) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.getBucketRealName(bucket), key);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    public String getUrl(String key, BucketType bucket) {
        return amazonS3Client.getUrl(this.getBucketRealName(bucket), key).toString();
    }


    private String putS3(File uploadFile, String fileName, BucketType bucketType) {
        amazonS3Client.putObject(new PutObjectRequest(this.getBucketRealName(bucketType), fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(this.getBucketRealName(bucketType), fileName).toString();
    }


    private ObjectMetadata setObjectMetaDataByBufferLen(byte[] buffer) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/png");
        objectMetadata.setContentLength(buffer.length);
        return objectMetadata;
    }


    public void setAcl(String bucketName) {
        AccessControlList list = new AccessControlList();
        list.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        list.setOwner(new Owner());
        amazonS3Client.setBucketAcl(bucketName, list);
    }


    public boolean existsBucket(String bucketName) {
        if(amazonS3Client.doesBucketExistV2(bucketName)) {
            return true;
        }
        return false;
    }

    public String putImage(MultipartFile multipartFile, BucketType bucketType) throws Exception {
        File convertedFile;
        try {
            String key = multipartFile.getOriginalFilename();
            convertedFile = convert(multipartFile);
            String url = putS3(convertedFile, key, bucketType);
            removeNewFile(convertedFile);
            return url;
        } catch (AmazonS3Exception e) {
            System.err.println(e.getErrorMessage());
            throw e;
        }
    }

    public String putImage(MultipartFile multipartFile, BucketType bucketType, String key) {
        File convertedFile;
        try {
            String path = "/profile_image/" + key;
            convertedFile = convert(multipartFile);
            String url = putS3(convertedFile, path, bucketType);
            removeNewFile(convertedFile);
            return url;
        } catch (AmazonS3Exception | IOException e) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.S3_IMAGE_UPLOAD_ERROR, e.getMessage());
        }
    }

    private String putS3(InputStream is, String key, ObjectMetadata meta, BucketType bucketType) {
        amazonS3Client.putObject(new PutObjectRequest(this.getBucketRealName(bucketType), key, is, meta)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(this.getBucketRealName(bucketType), key).toString();
    }

    public void deleteByKey(BucketType bucketType, String key) {
        amazonS3Client.deleteObject(this.getBucketRealName(bucketType), key);
    }

    public void deleteByKey(String bucketRealName, String key) {
        amazonS3Client.deleteObject(bucketRealName, key);
    }


    public void deleteByUrl(BucketType bucketType, String url) {
        String bucket = this.getBucketRealName(bucketType);
        String key = getKeyByUrl(bucket, url);
        deleteByKey(bucket, key);
    }

    public String getKeyByUrl(String bucket, String url) {
        int index = url.indexOf(bucket);

        return url.substring(index + bucket.length());
    }

    public void deleteUserBookImage(BucketType bucketType, String key) {
        amazonS3Client.deleteObject(this.getBucketRealName(bucketType), USER_BOOK_IMAGE_KEY_PREFIX + "/" + key);
    }

    public String putS3ByBufferImage(BufferedImage bufferedImage, String key, BucketType bucketType) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        byte[] buffer = os.toByteArray();
        InputStream is = new ByteArrayInputStream(buffer);
        ObjectMetadata objectMetadata = setObjectMetaDataByBufferLen(buffer);
        return putS3(is, key, objectMetadata, bucketType);
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            return;
        }
        log.info("임시 파일이 삭제 되지 못했습니다. 파일 이름: {}", targetFile.getName());
    }

    private File convert(MultipartFile file) throws IOException {
        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));//TEMP_FILE_PATH +
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환이 실패했습니다. 파일 이름: %s", file.getName()));
    }

    public BufferedImage drawingReductionCenterPosImg(BufferedImage detail, int width, int height) {
        BufferedImage bufferedImage =
                new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bufferedImage.createGraphics();

        g.drawImage(detail,
                0,0,
                width ,height, null);
        g.dispose();
        return bufferedImage;
    }
}
