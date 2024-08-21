package com.example.project.user.enums;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PhoneAuthKeys {
    PHONE_AUTH_SIGNUP_KEY("auth-signup-phone:"),
    PHONE_AUTH_EMAIL_KEY("auth-email-phone:"),
    PHONE_AUTH_PASSWORD_KEY("auth-password-phone:"),
    ;
    private final String value;

    public String getValue() {
        return value;
    }
}
