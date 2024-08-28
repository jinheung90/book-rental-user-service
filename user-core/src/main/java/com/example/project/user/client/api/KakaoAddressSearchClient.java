package com.example.project.user.client.api;

import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.user.client.dto.KakaoAddressSearchDto;
import com.example.project.user.client.dto.KakaoToken;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAddressSearchClient {

    @Value("${kakao-auth.rest-api-key}")
    private String restApiKey;

    private final RestTemplate restTemplate;

    public KakaoAddressSearchDto.Documents findOneByNameAndZoneNo(String addressName, String zoneNo) {

        UriComponents uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com")
                .path("/v2/local/search/address.json")
                .queryParam("query", addressName)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, String.format("KakaoAK %S", restApiKey));
        ResponseEntity<KakaoAddressSearchDto> response = restTemplate.postForEntity(uri.toUri(), new HttpEntity<>(headers), KakaoAddressSearchDto.class);
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.SEVER_ERROR, "kakao address search api fail");
        }
        final List<KakaoAddressSearchDto.Documents> documents = response.getBody().getDocuments();
        return getAddressFromZoneNoAndAddressName(addressName, zoneNo, documents);
    }

    private KakaoAddressSearchDto.Documents getAddressFromZoneNoAndAddressName(String addressName, String zoneNo, List<KakaoAddressSearchDto.Documents> documents) {
        for (KakaoAddressSearchDto.Documents doc: documents) {
            if(addressName.equals(doc.getAddress_name()) && zoneNo.equals(doc.getRoad_address().getZone_no())) {
                return doc;
            }
        }
        throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST,
                String.format("not exists addressName name: %s, zoneNo: %s", addressName, zoneNo));
    }
}