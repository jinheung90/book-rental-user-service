package com.example.project.aws.config;

import com.amazonaws.auth.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

@Configuration
class AwsConfig {
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider()  {
        return new DefaultAWSCredentialsProviderChain();
    }
}