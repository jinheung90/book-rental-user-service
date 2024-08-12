package com.example.project.common.util;


import java.util.HashMap;
import java.util.Map;

public class ResponseBody {
    public static Map<String, Object> successResponse() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }
}
