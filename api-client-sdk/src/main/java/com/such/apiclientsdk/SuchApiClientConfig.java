package com.such.apiclientsdk;

import com.such.apiclientsdk.client.SuchApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;

/**
 * 客户端配置
 *
 * @author SuchJack
 */
@Configuration
@ConfigurationProperties("such.client")
@Data
@ComponentScan
public class SuchApiClientConfig {

    private String accessKey;
    private String secretKey;

    @Bean
    public SuchApiClient suchApiClient(){
        return new SuchApiClient(accessKey,secretKey);
    }

    // 反射实例化 优点：运行时动态加载，但是性能不如直接实例化
//    @Bean
//    public SuchApiClient suchApiClient() {
//        SuchApiClient client = null;
//        try {
//            Class<?> forName = Class.forName("com.such.apiclientsdk.client.SuchApiClient");
//            Constructor<?> declaredConstructor = forName.getDeclaredConstructor(/*Integer.class, */String.class, String.class);
//            declaredConstructor.setAccessible(true);
//            client = (SuchApiClient) declaredConstructor.newInstance(/*appId, */accessKey, secretKey);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return client;
//    }
}
