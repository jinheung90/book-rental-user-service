package com.example.project.auth.security;

import com.example.project.errorHandling.ErrorResponse;
import com.example.project.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.errorHandling.errorEnums.IErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    private void sendErrorMessage(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN); // 권한 부족 403
        res.setContentType(MediaType.APPLICATION_JSON.toString());
        res.getWriter().write(this.objectMapper
                .writeValueAsString(ErrorResponse.response(((IErrorCode) GlobalErrorCode.NOT_ALLOW_USER).getCode(), "not allow this user not enough authority", HttpStatus.resolve(((IErrorCode) GlobalErrorCode.NOT_ALLOW_USER).getStatus()))));
    }
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        sendErrorMessage(response);
    }

}
