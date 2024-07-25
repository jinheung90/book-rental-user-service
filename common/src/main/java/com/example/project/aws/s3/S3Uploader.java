package com.example.project.aws.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    private static final String TEMP_FILE_PATH = "src/main/resources/";

    private final AwsCredentialsProvider awsCredentialsProvider;

    private AmazonS3Client amazonS3Client;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.profiles.active}")
    private String profile;

//    @PostConstruct
//    void init() {
//        this.amazonS3Client = AmazonS3Client
//                .builder()
//                .withCredentials((AWSCredentialsProvider) awsCredentialsProvider)
//                .withRegion(region)
//                .build();
//    }

    private String getBucketRealName(BucketType bucketType) {
        return profile + '-' + bucketType.getName();
    }

    public void deleteS3ByKey(String fileName, BucketType bucket) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.getBucketRealName(bucket), fileName);
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

    public void createBucket(String bucketName) {
        if (!amazonS3Client.doesBucketExistV2(bucketName)) {
            amazonS3Client.createBucket(bucketName);
        }
    }




    public String putImage(MultipartFile multipartFile, BucketType bucketType) throws Exception {
        createBucket(this.getBucketRealName(bucketType));
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

    private String putS3(InputStream is, String fileName,ObjectMetadata meta, BucketType bucketType) {
        amazonS3Client.putObject(new PutObjectRequest(this.getBucketRealName(bucketType), fileName, is, meta)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(this.getBucketRealName(bucketType), fileName).toString();
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
        File convertFile = new File(file.getOriginalFilename());//TEMP_FILE_PATH +
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환이 실패했습니다. 파일 이름: %s", file.getName()));
    }


//    public BufferedImage drawingCenterPosImg(BufferedImage origin, int width, int height, CustomRectangle customRectangle) {
//        BufferedImage bufferedImage =
//                new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
//        Graphics2D g = bufferedImage.createGraphics();
////        public abstract boolean drawImage(Image img,
////        int dx1, int dy1, int dx2, int dy2,
////        int sx1, int sy1, int sx2, int sy2,
////        ImageObserver observer);
//        int cropX = customRectangle.getCenterX() - width / 2;
//        int cropY = customRectangle.getCenterY() - height / 2;
//        g.drawImage(origin,
//                0,0, width ,height,
//                cropX, cropY,
//                cropX + width,cropY + height,
//                null);
//        g.dispose();
//        return bufferedImage;
//    }

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
