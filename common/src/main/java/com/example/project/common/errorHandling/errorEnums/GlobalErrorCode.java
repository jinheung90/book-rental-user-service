package com.example.project.common.errorHandling.errorEnums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum GlobalErrorCode implements IErrorCode {
    BAD_REQUEST("bad request", 400),
    SEND_FAIL("not connected child server", 500),
    ASYNC_FAIL("ASYNC_FAIL", 500),
    SQL_ERROR("SQL_ERROR", 500),
    IO_ERROR("io error", 500),
    ALREADY_USER_NICK("already exist nickname",  400),
    EXIST_EMAIL("already exist email",  400),
    EXIST_NICKNAME("already exists nickname", 400),
    EMAIL_REGEX_NOT_MATCH("email regex not match", 400),
    PASSWORD_NOT_MATCH("password not match", 400),
    NOT_EXISTS_USER("not exists user", 400),
    EXISTS_USER("exists user", 400),
    NOT_ALLOW_USER("not allow user", 403),
    NOT_VALID_TOKEN("not valid access token", 401),
    S3_IMAGE_UPLOAD_ERROR("s3 upload error", 400),
    PHONE_AUTH_NUM_EXPIRED("phone auth expired", 400),
    PHONE_AUTH_NUM_NOT_MATCH("phone auth not match",400),
    NOT_SUPPORT_LOGIN_PROVIDER("not support provider", 400),
    SEVER_ERROR("server error", 500),
    NAVER_API_FAIL("naver api fail", 500),
    ;

    private String message;
    private int status;

    @Override
    public String getMessage() {
        return this.message;
    }
    @Override
    public String getCode() {
        return this.toString();
    }
    @Override
    public int getStatus() {
        return this.status;
    }
}
