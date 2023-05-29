package com.starcloud.ops.server;


import com.starcloud.ops.business.core.config.BusinessAppProperties;
import com.starcloud.ops.business.core.config.StarcloudServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Configuration
@PropertySource(value = {"classpath:starcloud-llm-config.properties", "classpath:starcloud-llm-config-${spring.profiles.active}.properties"}, ignoreResourceNotFound = true)
@EnableConfigurationProperties(value = {StarcloudServerProperties.class, BusinessAppProperties.class})
public class StarcloudServerConfiguration {


}
