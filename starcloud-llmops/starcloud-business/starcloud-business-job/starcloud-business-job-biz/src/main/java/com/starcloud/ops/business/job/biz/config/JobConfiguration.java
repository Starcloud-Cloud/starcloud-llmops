package com.starcloud.ops.business.job.biz.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.powerjob.client.PowerJobClient;

@Slf4j
@Configuration
public class JobConfiguration {


    @Value("${powerjob.worker.server-address}")
    private String domain;

    @Value("${powerjob.worker.app-name}")
    private String appName;

    @Value("${powerjob.worker.app-password}")
    private String password;

    @Bean
    public PowerJobClient powerJobClient() {
        log.info("init powerJob client: {}  {}  {}", domain, appName, password);
        return new PowerJobClient(domain, appName, password);
    }
}
