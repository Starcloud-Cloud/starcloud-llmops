package com.starcloud.ops.business.core.config.oss;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Component
@ConfigurationProperties(prefix = "starcloud-llm.business.app.oss")
public class OssProperties {

    /**
     * 阿里云OSS服务的accessKey
     */
    private String accessKey;

    /**
     * 阿里云OSS服务的secretKey
     */
    private String secretKey;

    /**
     * 阿里云OSS服务的endpoint
     */
    private String endpoint;
}
