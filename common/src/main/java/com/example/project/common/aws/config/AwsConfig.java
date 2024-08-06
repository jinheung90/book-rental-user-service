package com.example.project.common.aws.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

@Configuration
class AwsConfig {
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider()  {
        return DefaultCredentialsProvider.builder().build();
    }
}