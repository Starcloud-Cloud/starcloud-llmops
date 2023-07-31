package com.starcloud.ops.business.core.config;


import com.starcloud.ops.business.core.config.translate.TranslatorProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author nacoyer
 */
@ConfigurationProperties(prefix = "starcloud-llm.business.app")
@Data
public class BusinessAppProperties {

    private String test;

    /**
     * 翻译服务配置
     */
    private TranslatorProperties translator;
}
