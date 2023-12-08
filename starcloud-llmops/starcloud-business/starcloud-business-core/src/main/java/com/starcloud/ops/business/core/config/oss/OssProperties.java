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
     * 阿里云OSS服务的 Endpoint
     */
    private String endpoint;

    /**
     * 阿里云OSS服务的 Bucket
     */
    private String bucket;

    /**
     * 阿里云OSS服务是否支持CNAME
     */
    private Boolean supportCname = false;

    /**
     * 阿里云OSS服务的 AccessKey
     */
    private String accessKey;

    /**
     * 阿里云OSS服务的 SecretKey
     */
    private String secretKey;


}
