package com.starcloud.ops.business.core.config.notice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Data
@Component
@ConfigurationProperties(prefix = "starcloud-pay.notice.environment")
public class DingTalkNoticeProperties {

    /**
     * 阿里云翻译服务的accessKey
     */
    private String name;

    /**
     * 阿里云翻译服务的secretKey
     */
    private String smsCode;

}
