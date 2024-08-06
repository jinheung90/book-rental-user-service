package com.example.project.auth.client.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KakaoProfile {
    private Long id;
    private Kakao_account kakao_account;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Kakao_account {
        private String email;
        private String phone_number;
    }
}
