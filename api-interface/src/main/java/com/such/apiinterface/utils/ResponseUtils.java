package com.such.apiinterface.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

/**
 * 响应工具
 */
public class ResponseUtils {
    public static Map<String, Object> responseToMap(String response) {
        return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

}
