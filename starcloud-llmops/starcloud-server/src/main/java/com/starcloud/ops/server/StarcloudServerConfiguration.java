package com.starcloud.ops.server;


import com.starcloud.ops.business.core.config.BusinessAppProperties;
import com.starcloud.ops.business.core.config.BusinessLimitProperties;
import com.starcloud.ops.business.core.config.BusinessOrderProperties;
import com.starcloud.ops.server.config.StarcloudServerProperties;
import com.starcloud.ops.business.core.config.BusinessDatasetProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.starcloud.ops")
@PropertySource(value = {"classpath:config/starcloud-llm-config.properties","classpath:starcloud-llm-config.properties",
        "classpath:config/starcloud-llm-config-${spring.profiles.active}.properties","classpath:starcloud-llm-config-${spring.profiles.active}.properties"}, ignoreResourceNotFound = true)
@EnableConfigurationProperties(value = {StarcloudServerProperties.class, BusinessAppProperties.class, BusinessDatasetProperties.class, BusinessLimitProperties.class, BusinessOrderProperties.class})
public class StarcloudServerConfiguration {

    @PostConstruct
    public void init() {
        log.info("init StarCloud-LLMops ...... ");
    }
}
