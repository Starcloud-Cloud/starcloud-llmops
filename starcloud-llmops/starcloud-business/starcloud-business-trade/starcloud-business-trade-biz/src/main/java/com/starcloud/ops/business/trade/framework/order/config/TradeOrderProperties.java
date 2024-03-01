package com.starcloud.ops.business.trade.framework.order.config;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.api.tenant.TenantApi;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 交易订单的配置项
 *
 * @author LeeYan9
 * @since 2022-09-15
 */
@ConfigurationProperties(prefix = "yudao.trade.order")
@Data
@Validated
@Component
public class TradeOrderProperties {

    /**
     * 应用编号
     */
    @NotNull(message = "应用编号不能为空")
    private Long appId;

    public Long getAppId() {
        Long currentTenantId = TenantContextHolder.getTenantId();
        if (appIdAndTenant != null) {
            for (TenantAppConfig tenantAppConfig : appIdAndTenant) {
                if (tenantAppConfig.getTenantId().equals(currentTenantId)) {
                    return tenantAppConfig.getAppId();
                }
            }
        }
        return appId;
    }

    /**
     * 支付超时时间
     */
    @NotNull(message = "支付超时时间不能为空")
    private Duration payExpireTime;

    /**
     * 收货超时时间
     */
    @NotNull(message = "收货超时时间不能为空")
    private Duration receiveExpireTime;

    /**
     * 评论超时时间
     */
    @NotNull(message = "评论超时时间不能为空")
    private Duration commentExpireTime;

    /**
     * 评论超时时间
     */
    @NotNull(message = "租户与应用配置")
    private List<TenantAppConfig> appIdAndTenant;


    @PostConstruct
    public void init() {
        Long currentTenantId = TenantContextHolder.getTenantId();
        if (currentTenantId == null) {
            return;
        }
        updateAppIdByTenantId(currentTenantId);
    }

    /**
     * 根据租户ID更新应用ID
     *
     * @param currentTenantId 当前租户ID
     */
    public void updateAppIdByTenantId(Long currentTenantId) {
        if (appIdAndTenant != null) {
            for (TenantAppConfig tenantAppConfig : appIdAndTenant) {
                if (tenantAppConfig.getTenantId().equals(currentTenantId)) {
                    this.appId = tenantAppConfig.getAppId();
                    break;
                }
            }
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Validated
    public static class TenantAppConfig {

        @Schema(description = "应用编号")
        private Long appId;

        @Schema(description = "租户编号")
        private Long tenantId;
    }
}
