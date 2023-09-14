package com.starcloud.ops.llm.langchain.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "starcloud-langchain.model.llm.openai")
public class OpenAIConfig {

    private String apiKey;

    private Long timeOut;

    @Deprecated
    private String proxyHost;

    @Value("#{'${starcloud-langchain.model.llm.openai.proxyHost}'.split(',')}")
    private List<String> proxyHosts;

    private int proxyPort;

    private Boolean azure;

    private String azureKey;

//
//
//    public void setApiKey(String apiKey) {
//        OpenAIConfig.apiKey = apiKey;
//    }
//
//    public void setTimeOut(Long timeOut) {
//        OpenAIConfig.timeOut = timeOut;
//    }

}
