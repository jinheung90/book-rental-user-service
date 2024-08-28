package com.example.project.address.client.api;

import com.example.project.address.client.dto.KakaoAddressSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoAddressSearchClient {

    @Value("${kakao-auth.rest-api-key}")
    private String restApiKey;


    public KakaoAddressSearchDto findAllByAddress(String query) {
        return new KakaoAddressSearchDto();
    }

}