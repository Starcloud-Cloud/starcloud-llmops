package com.starcloud.ops.business.user.framework.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * 新用户判断的配置项
 *
 * @author Alancusack
 * @since 2024-02-27
 */
@ConfigurationProperties(prefix = "user-config.new-user")
@Data
@Validated
@Component
public class NewUserProperties {

    /**
     * 用户注册时间
     */
    @NotNull(message = "用户注册时间")
    private Duration registerTime;

    /**
     * 是否存在有效订单
     */
    @NotNull(message = "是否存在有效订单")
    private Boolean validOrder;

}
