package com.example.project.common.aws.sns;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class SnsSender {

    private SnsClient snsClient;
    private final Environment environment;
    private static final String PHONE_AUTH_MESSAGE_TEMPLATE = "인증 번호는 %s 입니다.";
    private static final String MESSAGE_FAIL_LOG_TEMPLATE = "실패 코드 %d, 응답 메세지: %s, 메세지 내용: %s";
    private static final Region MESSAGE_REGION = Region.AP_NORTHEAST_1;
    @Value("${spring.profiles.active}")
    private String profile;
    @PostConstruct
    void init() {

        if(profile.equals("local")) {
            String key = environment.getProperty("spring.cloud.aws.credentials.access-key");
            String value = environment.getProperty("spring.cloud.aws.credentials.secret-key");
            this.snsClient = SnsClient.builder()
                    .credentialsProvider(
                         StaticCredentialsProvider.create(AwsBasicCredentials.create(key, value)
                    )).region(MESSAGE_REGION)
                    .build();
            return;
        }

        this.snsClient = SnsClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .region(MESSAGE_REGION)
                .build();

    }

    public void sendPhoneAuthNumberMessage(String phoneNumber, String number) {
        final String messageContext = String.format(PHONE_AUTH_MESSAGE_TEMPLATE, number);

        PublishRequest publishRequest = PublishRequest
                .builder()
                .message(messageContext)
                .phoneNumber(phoneNumber)
                .build();

        PublishResponse publishResponse = this.snsClient.publish(publishRequest);

        if(!publishResponse.sdkHttpResponse().isSuccessful()) {
            final String error = String.format(
                    MESSAGE_FAIL_LOG_TEMPLATE,
                    publishResponse.sdkHttpResponse().statusCode(),
                    publishResponse.sdkHttpResponse().statusText().orElse(""),
                    messageContext
            );
            log.error(error);
            throw new RuntimeExceptionWithCode(GlobalErrorCode.SEND_FAIL, error);
        }

        log.info(publishResponse.messageId());
    }
}
