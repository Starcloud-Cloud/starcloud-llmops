package com.starcloud.ops.business.core.config.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2022-06-22
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * oss access key
     */
    private String accessKey;

    /**
     * oss secret key
     */
    private String secretKey;

    /**
     * oss endpoint
     */
    private String endpoint;

    /**
     * oss bucket name
     */
    private String bucket;

    /**
     * oss support cname
     */
    private boolean supportCname = false;
}
