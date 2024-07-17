package com.example.project.clients.api;


import com.example.project.clients.dto.KakaoProfile;
import com.example.project.clients.dto.KakaoTerm;
import com.example.project.clients.dto.KakaoToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final RestTemplate restTemplate;

    @Value("${kakao-auth.rest-api-key}")
    private String restApiKey;
    @Value("${kakao-auth.redirect-uri}")
    private String redirectUri;
    @Value("${kakao-auth.client-secret}")
    private String clientSecret;

    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

    public KakaoToken getKakaoTokenFromAuthorizationCode(String authorizationCode) {

        UriComponents uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", restApiKey)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", authorizationCode)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
        return restTemplate.postForObject(uri.toUri(), new HttpEntity<>(headers), KakaoToken.class);
    }

    public KakaoProfile fetchUserProfile(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
        ResponseEntity<KakaoProfile> profile = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, new HttpEntity<>(headers), KakaoProfile.class);
        return profile.getBody();
    }

    public KakaoTerm getKakaoTerms(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
        ResponseEntity<KakaoTerm> terms = restTemplate.exchange("https://kapi.kakao.com/v1/user/service/terms", HttpMethod.GET, new HttpEntity<>(headers), KakaoTerm.class);
        return terms.getBody();
    }
}
