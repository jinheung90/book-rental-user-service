package com.example.project.common.errorHandling.controllerAdvice;

import com.example.project.common.errorHandling.ErrorResponse;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ExceptWithErrorCodeAdvice {
    
    // 내 커스텀 용 에러 핸들러
    @ExceptionHandler(RuntimeExceptionWithCode.class)
    public ResponseEntity<HashMap<String, String>> all(final RuntimeExceptionWithCode e) {
        return ErrorResponse.response(e.getCode().getCode(), e.getMessage(), HttpStatus.resolve(e.getCode().getStatus()));
    }

    // request 잘못 됬을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final Map<String, String> responseMap = new HashMap<>() {{
            put("message", "no validations");
            put("error position", Objects.requireNonNull(e.getFieldError()).getField());
            put("CODE", "G001");
        }};
        return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(NumberFormatException.class)
    public Object handleNumFormatException(NumberFormatException e) {
        String errorMessage = e.getLocalizedMessage();

        final Map<String, String> responseMap = new HashMap<>() {{
            put("message", "no validations");
            put("detail", errorMessage);
            put("CODE", "G001");
        }};
        return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
    }
    //멀티 파트 파일 관련해서

}
