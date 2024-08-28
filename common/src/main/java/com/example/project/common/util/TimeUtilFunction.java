package com.example.project.common.util;


import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtilFunction {
    public static final ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
    public static ZonedDateTime getNowAtSeoul() {
        return ZonedDateTime.now(seoulZoneId);
    }
}
