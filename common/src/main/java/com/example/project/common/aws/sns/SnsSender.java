package com.example.project.common.aws.sns;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Component
public class SnsSender {

//    private SnsClient snsClient;
//
//    private static final String PHONE_AUTH_MESSAGE_TEMPLATE = "인증 번호는 %s 입니다.";
//
//    @PostConstruct
//    void init() {
//        this.snsClient = SnsClient.builder()
//                .credentialsProvider(DefaultCredentialsProvider.builder().build())
//                .build();
//    }
//
//    public void sendPhoneAuthNumberMessage(String number, String phoneNumber) {
//        String messageContext = String.format(PHONE_AUTH_MESSAGE_TEMPLATE, number);
//        final PublishRequest publishRequest = PublishRequest.builder()
//                .topicArn(topicArn)
//                .subject("HTTP ENDPOINT TEST MESSAGE")
//                .message(message.toString())
//                .build();
//    }
}
