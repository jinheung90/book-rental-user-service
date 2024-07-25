package com.example.project.aws.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
class AwsConfig {
//    cloud:
//    aws:
//    credentials:
//    access-key:
//    secret-key:
//
    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String secretKey;


    @Bean
    public AWSCredentialsProvider awsCredentialsProvider()  {
        if(profile.equals("local")) {
            return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        } else {
            return new DefaultAWSCredentialsProviderChain();
        }
    }
}