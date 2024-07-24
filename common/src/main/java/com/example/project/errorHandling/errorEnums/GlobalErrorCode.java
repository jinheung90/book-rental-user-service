package com.example.project.errorHandling.errorEnums;

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
    NOT_ALLOW_MENTOR("not allow progress mentor",400),
    EXIST_EMAIL("already exist email",  400),
    ALREADY_WITHDRAWAL("already withdrawal",  400),
    ALREADY_EXIST_USER("already exist user", 400),
    DUPLICATE_EMAIL("email duplicate",  400),
    NOT_EXISTS_REGION("not exists region", 400),
//    EXIST_NICKNAME("nickname is duplicate", 400),
    NOT_EXISTS_USER("not exists user", 400),
    NOT_ALLOW_USER("not allow user", 400),
    NOT_VALID_TOKEN("not valid access token", 400),
    NOT_EXISTS_MENTOR("not exist mentor but required", 400),
    NOT_EXISTS_RESERVATION_WITH_SAME_MENTEE_AND_ID("not exists reservation id and mentee", 400),
    NOT_EXISTS_RESERVATION_WITH_SAME_MENTOR_AND_ID("not exists reservation id and mentor", 400),
    RESERVATION_NOT_ALLOW_DATE_PERIOD("not allowed date period", 400),
    RESERVATION_NOT_ALLOW_DATE_AVAILABLE_DAY("not allowed date day term", 400),
    ALREADY_RESERVATION("already reservation", 400),
    NOT_ALLOW_RESERVATION_WITH_ME("valid reservation to me", 400),
    ALREADY_EXISTS_DAY_OF_WEEK_SCHEDULE("already exists default schedule", 400),
    NOT_ALLOW_GET_SCHEDULE_OVER_LIMIT_DAYS("when mentor schedule get but days is too many", 400),
    EMAIL_AUTH_NOT_EXIST_DOMAIN("not exists domain", 400),
    GRADE_TOO_LOW("grade is too low",400),
    EMAIL_AUTH_NOT_MATCH_NUMBER("not match num", 400),
    NOT_EQUAL_PRICE("not match price", 400),
    NOT_MATCH_LINK_PASSWORD("not match link password", 400),
    ALREADY_MENTOR_APP("already mentor exists", 400),
    SCHEDULE_START_END_SAME("schedule is same", 400),
    ALREADY_MENTOR_ACCEPT("already mentor accept", 400),
    NOT_VALID_COUPON("not valid coupon", 400),
    COUPON_ACTIVITY_IS_NOT("coupon active is not", 400),
    ALREADY_MENTOR_REVIEW_WRITE("이미 작성하였습니다", 400),
    MENTORING_IS_NOT_END_WHEN_REVIEW("mentoring is not over", 400)
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
