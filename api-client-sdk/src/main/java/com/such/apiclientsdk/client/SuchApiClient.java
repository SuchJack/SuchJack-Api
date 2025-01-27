package com.such.apiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.such.apiclientsdk.model.Api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.such.apiclientsdk.utils.SignUtils.genSign;

/**
 * 调用网关请求配置
 *
 * @author SuchJack
 */
public class SuchApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090/api/v1"; // 网关地址

//    private final Integer appId; // 区分业务线
    private final String accessKey;
    private final String secretKey;

    public SuchApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 调用第三方接口
     * @param api api对象
     * @return String
     */
    public String getResult(Api api) {
        String json = JSONUtil.toJsonStr(api.getBody());
        if ("get".equals(api.getMethod()) || "GET".equals(api.getMethod())) {
            return HttpRequest.get(GATEWAY_HOST)
                    .header("Accept","application/json;charset=UTF-8")
                    .addHeaders(getHeadersMap(json,api.getInterfaceId(),api.getUrl()))
                    .charset("UTF-8")
                    .body(json)
                    .execute()
                    .body();
        } else {
            return HttpRequest.post(GATEWAY_HOST)
                    .header("Accept","application/json;charset=UTF-8")
                    .addHeaders(getHeadersMap(json,api.getInterfaceId(),api.getUrl()))
                    .charset("UTF-8")
                    .body(json)
                    .execute()
                    .body();
        }
    }

    /**
     * 创建一个私有方法，用于构造请求头(accessKey、随机数、请求内容、时间戳、签名密钥)
     *
     * @param body 请求内容
     * @return Map<String, String>
     */
    private Map<String, String> getHeadersMap(String body,String interfaceId,String url) {
        Map<String, String> hashmap = new HashMap<>();
        // userId、userAccount ???
        hashmap.put("accessKey", accessKey); // 参数1 accessKey
        // 将 "secretKey" 和其对应的值放入 map 中
        // 注意：不能直接发送密钥
//        hashmap.put("secretKey",secretKey);
        hashmap.put("nonce", RandomUtil.randomNumbers(4)); // 参数2 随机数
//        String encodedBody = SecureUtil.md5(body);  // TODO 内容是否需要加密？
        hashmap.put("body", URLEncoder.encode(body, StandardCharsets.UTF_8)); // 参数3 请求内容 -> 参数传入!
        hashmap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000)); // 参数4 时间戳
        hashmap.put("sign", genSign(body, secretKey)); // 参数5 签名密钥
        hashmap.put("interfaceId", interfaceId); // 参数6 接口ID
        hashmap.put("url", url); // 参数7 接口ID
        return hashmap;
    }
}
