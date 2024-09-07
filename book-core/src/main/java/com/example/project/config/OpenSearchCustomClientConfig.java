package com.example.project.config;


import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;

import jdk.jfr.ContentType;
import org.apache.http.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenSearchCustomClientConfig extends AbstractOpenSearchConfiguration {

    @Value("#{'${spring.elasticsearch.uris}'.split(',')}")
    private String[] uris;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.profiles.active}")
    private String profile;


    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {

        if(profile.equals("local")) {
            return RestClients.create(ClientConfiguration.builder()
                    .connectedTo(uris)
                    .build()
            ).rest();
        }
        return RestClients.create(ClientConfiguration.builder()
                .connectedTo(uris)
                .withBasicAuth(username, password)
                .build()
        ).rest();
    }

}