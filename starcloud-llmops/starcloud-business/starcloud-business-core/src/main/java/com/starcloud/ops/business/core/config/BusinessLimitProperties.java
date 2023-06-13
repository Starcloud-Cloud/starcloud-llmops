package com.starcloud.ops.business.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "starcloud-llm.business.limit")
@Data
public class BusinessLimitProperties {


    private String test;
}
