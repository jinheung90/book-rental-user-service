package com.example.project.auth.client.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KakaoTerm {
    private List<AllowedServiceTerm> allowed_service_terms;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class AllowedServiceTerm{
        private String tag;
        private LocalDateTime agreed_at;
    }

}
