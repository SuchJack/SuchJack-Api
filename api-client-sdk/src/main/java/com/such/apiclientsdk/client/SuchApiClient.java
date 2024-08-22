package com.such.apiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.such.apiclientsdk.constants.ErrorCode;
import com.such.apiclientsdk.model.Api;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.such.apiclientsdk.utils.SignUtils.genSign;


public class SuchApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";

    private final String accessKey;
    private final String secretKey;

    public SuchApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 通用返回结果
     * @param api api对象
     * @return 返回结果
     */
    public String getResult(Api api) {
        String json = JSONUtil.toJsonStr(api.getBody());
        if (json == null) {
            json = ErrorCode.NULL_JSON;
        }
        String pathFromUrl = this.getPathFromUrl(api.getUrl());
        if ("get".equals(api.getMethod()) || "GET".equals(api.getMethod())) {
            HttpResponse httpResponse = HttpRequest
                    .get(GATEWAY_HOST + pathFromUrl)
                    .header("Accept", "application/json;charset=utf-8")
                    .addHeaders(getHeadeMap(json, api.getInterfaceId(),api.getUrl()))
                    .execute();
            return httpResponse.body();
        }
        if (api.getMethod().equals("post") || api.getMethod().equals("POST")) {
            HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + pathFromUrl)
                    .header("Accept", "application/json;charset=utf-8")
                    .addHeaders(getHeadeMap(json,api.getInterfaceId(),api.getUrl()))
                    .body(json)
                    .execute();
            return httpResponse.body();
        }
        else return null;
    }

    /**
     * 从url中获取请求路径
     * 示例：<a href="http://localhost:8000/api/invoke"></a> -> /api/invoke
     *
     * @param url URL
     * @return 请求路径path
     */
    private String getPathFromUrl(String url) {
        try {
            url = url.trim();  // 清除两端空格
            URI uri = new URI(url);
            return uri.getPath();
        } catch (URISyntaxException e) {
            System.err.println("Invalid URL syntax: " + url);
            e.printStackTrace();
            return "/defaultPath";  // 或者返回一个默认路径
        }
    }

    /**
     * 创建一个私有方法，用于构造请求头(accessKey、随机数、请求内容、时间戳、签名密钥)
     *
     * @param body 请求内容
     * @return
     */
    private Map<String, String> getHeadeMap(String body,String interfaceId,String url) {
        // 创建一个新的 HashMap 对象
        Map<String, String> hashmap = new HashMap<>();
        // 将 "accessKey" 和其对应的值放入 map 中
        hashmap.put("accessKey", accessKey); // 参数1 accessKey
        // 将 "secretKey" 和其对应的值放入 map 中
        // 注意：不能直接发送密钥
//        hashmap.put("secretKey",secretKey);
        // 生成随机数(生产一个包含4个随机数字的字符串)
        hashmap.put("nonce", RandomUtil.randomNumbers(4)); // 参数2 随机数
        // 请求体内容
        hashmap.put("body", URLEncoder.encode(body, StandardCharsets.UTF_8)); // 参数3 请求内容 -> 参数传入!
        // 当前时间戳
        // System.currentTimeMillis()返回当前时间的毫秒数。通过除以1000，可以将毫秒数转换为秒数，以得到当前时间戳的秒数
        // String.valueOf()方法用于将数值转换为字符串。在这里，将计算得到的时间戳(以秒为单位)转换为字符串
        hashmap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 参数4 时间戳
        hashmap.put("sign", genSign(body, secretKey)); // 参数5 签名密钥
        // 返回构造的请求头
        hashmap.put("interfaceId", interfaceId); // 参数6 接口ID
        hashmap.put("url", url); // 参数7 接口ID
        return hashmap;
    }
}
