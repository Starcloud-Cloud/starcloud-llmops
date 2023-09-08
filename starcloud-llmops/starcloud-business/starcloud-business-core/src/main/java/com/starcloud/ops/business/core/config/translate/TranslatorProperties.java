package com.starcloud.ops.business.core.config.translate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Component
@ConfigurationProperties(prefix = "starcloud-llm.business.app.translator")
public class TranslatorProperties {

    /**
     * 阿里云翻译服务配置
     */
    private AliyunTranslatorProperties aliyun;


    @Data
    @ToString
    @NoArgsConstructor
    @EqualsAndHashCode
    @Component
    @ConfigurationProperties(prefix = "starcloud-llm.business.app.translator.aliyun")
    public static class AliyunTranslatorProperties {

        /**
         * 阿里云翻译服务的accessKey
         */
        private String accessKey;

        /**
         * 阿里云翻译服务的secretKey
         */
        private String secretKey;

        /**
         * 阿里云翻译服务的endpoint
         */
        private String endpoint;
    }
}
