package com.starcloud.ops.business.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "starcloud-llm.business.poster")
@Data
public class BusinessPosterProperties {

    private String test;
}
