package com.starcloud.ops.business.core.config.xhs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Component
@ConfigurationProperties(prefix = "starcloud-llm.business.app.red-book")
public class RedBookProperties {

    private String appKey;

    private String appSecret;
}
