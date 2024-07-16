package com.example.project.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BizRequest {
    private String account;
    private String refkey;
    private String type;
    private String from;
    private String to;
    private Content content;
    private Resend resend;
    private Recontent recontent;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Resend {
        private String first;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Recontent {
        private Lms lms;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        private Sms sms;
        private AT at;
        private Lms lms;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sms  {
        private String message;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AT  {
        private String message;
        private String senderkey;
        private String templatecode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Lms  {
        private String message;
        private String subject;
    }

}
