package com.starcloud.ops.business.app;

import com.aliyun.ocr_api20210707.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author nacoyer
 */
@Configuration
@EnableFeignClients(basePackages = "com.starcloud.ops.business.app.feign")
public class BusinessAppConfiguration {

    @Value("${starcloud-llm.ocr.aliyun.accessKey}")
    private String accessKey;

    @Value("${starcloud-llm.ocr.aliyun.accessSecret}")
    private String accessSecret;

    @Value("${starcloud-llm.ocr.aliyun.endpoint}")
    private String endpoint;


    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Client client() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKey)
                .setAccessKeySecret(accessSecret)
                .setEndpoint(endpoint)
                ;
        return new Client(config);
    }

}
