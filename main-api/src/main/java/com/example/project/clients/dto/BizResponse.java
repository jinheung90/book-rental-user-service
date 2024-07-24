package com.example.project.clients.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BizResponse {
    private String accesstoken;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class BizResponseResult {
        private String RESULT;
    }

}
