package com.example.project.book.client.api;

import com.example.project.book.client.dto.NaverBookSearchDto;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverBookSearchClient {

    private final RestTemplate restTemplate;

    @Value("${naver.client-id}")
    private String clientId;
    @Value("${naver.client-secret}")
    private String clientSecret;

    private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON.toString();
    private static final String CLIENT_ID_HEADER_NAME = "X-Naver-Client-Id";
    private static final String CLIENT_SECRET_HEADER_NAME = "X-Naver-Client-Secret";

    public NaverBookSearchDto getBooksFromName(int start, int display, String name) {

        UriComponents uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/book.json")
                .queryParam("query",name)
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", "sim")
                .build();
        log.info(clientId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
        headers.add(CLIENT_ID_HEADER_NAME, clientId);
        headers.add(CLIENT_SECRET_HEADER_NAME, clientSecret);
        return restTemplate.getForObject(uri.toUriString(), NaverBookSearchDto.class, new HttpEntity<>(headers));
    }
}
