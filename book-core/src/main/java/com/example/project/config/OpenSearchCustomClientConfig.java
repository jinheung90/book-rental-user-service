package com.example.project.config;

import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.example.project.book.search.repository"})
@Slf4j
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
                    .connectedToLocalhost()
                    .build();
            return RestClients.create(clientConfiguration).rest();

        }
        clientConfiguration = ClientConfiguration.builder()
                .connectedTo(uris)
                .usingSsl()
                .withBasicAuth(username, password)
                .build();
        clientConfiguration.getEndpoints().forEach(
                inetSocketAddress -> {
                    log.info(inetSocketAddress.getHostName());
                    log.info(String.valueOf(inetSocketAddress.getPort()));
                    log.info(inetSocketAddress.getHostString());
                }
        );
        return RestClients.create(clientConfiguration).rest();
    }

}