package com.example.project.book.client.api;

import com.example.project.book.client.dto.NaverBookSearchDto;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

    private static final String CLIENT_ID_HEADER_NAME = "X-Naver-Client-Id";
    private static final String CLIENT_SECRET_HEADER_NAME = "X-Naver-Client-Secret";

    public NaverBookSearchDto getBooksFromName(int start, int display, String name) {

        UriComponents uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/book.json")
                .queryParam("query", name)
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", "sim")
                .encode()
                .build();
        log.info(uri.toUriString());

        RequestEntity requestEntity = RequestEntity.get(uri.toUri())
                .header(CLIENT_ID_HEADER_NAME, clientId)
                .header(CLIENT_SECRET_HEADER_NAME, clientSecret)
                .build();
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        String jsonStr = response.getBody();

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error(response.toString());
            throw new RuntimeExceptionWithCode(GlobalErrorCode.NAVER_API_FAIL, jsonStr);
        }

        try {
            return new ObjectMapper().readValue(response.getBody(), NaverBookSearchDto.class);
        } catch (JsonProcessingException e) {
            log.error(e.getLocalizedMessage());
            log.error(jsonStr);
            throw new RuntimeExceptionWithCode(GlobalErrorCode.NAVER_API_FAIL, "string to json parse fail");
        }
    }
}
