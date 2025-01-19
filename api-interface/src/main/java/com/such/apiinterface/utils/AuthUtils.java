package com.such.apiinterface.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 校验器
 */
@Component
public class AuthUtils {

    /**
     * 校验用户是否合法 // TODO
     * @param headers 请求头
     * @return boolean
     */
    public boolean isAuth(Map<String, String> headers) {
        return true;
    }

    /**
     * 获取请求头中的信息
     *
     * @param request 请求
     * @return Map<String, String>
     */
    public Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
//        map.put("userId", request.getHeader("userId"));
//        map.put("userAccount", request.getHeader("userAccount"));
//        map.put("appId", request.getHeader("appId"));
        map.put("accessKey", request.getHeader("accessKey"));
//        map.put("secretKey", request.getHeader("secretKey"));
        map.put("body", request.getHeader("body"));
        map.put("timestamp", request.getHeader("timestamp"));
        map.put("interfaceId", request.getHeader("interfaceId"));
        map.put("url", request.getHeader("url"));
        return map;
    }

}
