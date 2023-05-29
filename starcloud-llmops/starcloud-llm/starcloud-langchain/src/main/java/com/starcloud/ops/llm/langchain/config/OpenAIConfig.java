package com.starcloud.ops.llm.langchain.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class OpenAIConfig {

    public static String apiKey;

    public static Long timeOut;

    @Value("${OpenAIConfig.apiKey}")
    public void setApiKey(String apiKey) {
        OpenAIConfig.apiKey = apiKey;
    }

    @Value("${OpenAIConfig.timeOut}")
    public void setTimeOut(Long timeOut) {
        OpenAIConfig.timeOut = timeOut;
    }

}
