package com.example.project.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public class ErrorResponse {
    public static ResponseEntity<HashMap<String, String>> response(String code, String message, HttpStatus status) {
        HashMap<String,String> responseMap = new HashMap<>();
        responseMap.put("message", message);
        responseMap.put("CODE", code);

        return ResponseEntity
            .status(status)
            .body(responseMap);
    }
}
