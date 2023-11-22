package com.starcloud.ops.business.app;

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

    @LoadBalanced
    public RestTemplate restTemplate() {
        // FIXME: 2023/11/22  feign 新版本已经存在 考虑替换方案
        return new RestTemplate();
    }

}
