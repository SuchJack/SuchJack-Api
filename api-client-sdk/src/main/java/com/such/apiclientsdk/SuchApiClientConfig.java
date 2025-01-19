package com.such.apiclientsdk;

import com.such.apiclientsdk.client.SuchApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
}
