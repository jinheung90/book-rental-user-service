package com.example.project.config;

import jdk.jfr.ContentType;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.example.project.book.search.repository"})
@Slf4j
public class ElasticsearchCustomClientConfig extends ElasticsearchConfiguration {

    @Value("#{'${spring.elasticsearch.uris}'.split(',')}")
    private String[] uris;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(uris)
                .usingSsl()
                .withBasicAuth(username, password)
                .withClientConfigurer((ElasticsearchClients.ElasticsearchHttpClientConfigurationCallback) clientConfigurer ->
                        HttpAsyncClientBuilder.create()
                                .disableAuthCaching()
                                .addInterceptorLast(((HttpResponseInterceptor) (response, context) ->
                                        response.addHeader("X-Elastic-Product", "Elasticsearch")))
                                .addInterceptorLast((HttpRequestInterceptor) (request, context) ->
                                        request.setHeader("Content-Type", "application/json"))
                )
                .build();
    }
}