package com.example.project.config;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.opensearch.gateway.GatewayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        ClientConfiguration clientConfiguration;
        if(profile.equals("local")) {
            clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(uris)
                    .build();
        } else {
            clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(uris)
                    .withBasicAuth(username, password)
                    .build();
        }
        try {
            return RestClients.create(clientConfiguration).rest();
        } catch (Exception e) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.SEVER_ERROR);
        }
    }
}