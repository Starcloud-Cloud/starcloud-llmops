package com.starcloud.ops.business.extend.framework.feishu.config;

// TODO @芋艿：未来要不要放数据库中？考虑 saas 多租户时，不同租户使用不同的配置？

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * 交易运费快递的配置项
 *
 * @author jason
 */
@Component
@ConfigurationProperties(prefix = "starcloud.extend.feishu")
@Data
@Validated
public class FeiShuProperties {

    /**
     * 应用编号
     */
    @NotEmpty(message = "飞书应用编号")
    private String appId;
    /**
     * 应用密钥
     */
    @NotEmpty(message = "飞书应用密钥")
    private String secret;

    /**
     * 多维表编号
     */
    @NotEmpty(message = "飞书多维表Token")
    private String appToken;

    @NotEmpty(message = "表编号")
    private String tableId;

}
