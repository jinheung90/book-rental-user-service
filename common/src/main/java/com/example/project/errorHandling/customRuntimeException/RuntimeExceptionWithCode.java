package com.example.project.errorHandling.customRuntimeException;


import com.example.project.errorHandling.errorEnums.IErrorCode;

public class RuntimeExceptionWithCode extends RuntimeException {

    private final IErrorCode code;
    private final String message;

    public RuntimeExceptionWithCode(IErrorCode code) {
        this.message = code.getMessage();
        this.code = code;
    }

    public RuntimeExceptionWithCode(IErrorCode code, String customMessage) {
        this.code = code;
        this.message = customMessage;
    }

    public IErrorCode getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return message;
    }


}
