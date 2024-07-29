package com.example.project.auth.dto;


import com.example.project.enums.LoginProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {


    public static LoginResponse emptyResponse() {
        return LoginResponse.builder().build();
    }
}
