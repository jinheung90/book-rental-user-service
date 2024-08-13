package com.example.project.user.client.dto;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KakaoProfile {
    private Long id;
    private Kakao_account kakao_account;

    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Kakao_account {
        private String email;
        private String phone_number;
    }
}
