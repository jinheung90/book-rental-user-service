package com.example.project.user.security;

import com.example.project.common.errorHandling.ErrorResponse;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.common.errorHandling.errorEnums.IErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.querydsl.core.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;


    private void sendErrorMessage(HttpServletResponse res) throws IOException {
        res.setStatus(GlobalErrorCode.NOT_VALID_TOKEN.getStatus()); // 인가 부족 401
        res.setContentType(MediaType.APPLICATION_JSON.toString());
        String message = "not valid token";
        if(!StringUtils.isNullOrEmpty(res.getHeader("jwt-Expired"))) {
            message = "jwt expired";
        }
        res.getWriter().write(this.objectMapper
                .writeValueAsString(ErrorResponse.response(((IErrorCode) GlobalErrorCode.NOT_VALID_TOKEN).getCode(), message, HttpStatus.resolve(((IErrorCode) GlobalErrorCode.NOT_VALID_TOKEN).getStatus()))));
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        sendErrorMessage(response);
    }
}
