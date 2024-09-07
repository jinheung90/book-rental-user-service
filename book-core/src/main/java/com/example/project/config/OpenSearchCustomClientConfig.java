package com.example.project.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;

import jdk.jfr.ContentType;
import org.apache.http.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.project.book.search.repository")
public class OpenSearchCustomClientConfig {

    @Value("${spring.elasticsearch.host}")
    private String host;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        HttpHost httpHost;
        if(profile.equals("local")) {
            httpHost = new HttpHost("localhost", 9200, "http");
        } else {
            httpHost = new HttpHost(host, 443, "https");
        }
        final RestClient restClient = RestClient.builder(new HttpHost(httpHost))
                .setDefaultHeaders(
                        new Header[]{new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)}
                ).setHttpClientConfigCallback(
                        httpClientBuilder -> httpClientBuilder.addInterceptorLast((HttpResponseInterceptor) (response, context) -> {
                            response.addHeader("X-Elastic-Product", "Elasticsearch");
                        })
                )
                .build();
        return new  ElasticsearchClient(
                new RestClientTransport(
                        restClient, new JacksonJsonpMapper()
                )
        );
    }

}