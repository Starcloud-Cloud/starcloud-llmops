package com.starcloud.ops.business.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "starcloud-llm.server")
@Data
public class StarcloudServerProperties {


    private String test;


}
