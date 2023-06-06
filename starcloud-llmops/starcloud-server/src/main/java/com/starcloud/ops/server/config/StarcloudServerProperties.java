package com.starcloud.ops.server.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "starcloud-llm.server")
@Data
public class StarcloudServerProperties {


    private String test;


}
