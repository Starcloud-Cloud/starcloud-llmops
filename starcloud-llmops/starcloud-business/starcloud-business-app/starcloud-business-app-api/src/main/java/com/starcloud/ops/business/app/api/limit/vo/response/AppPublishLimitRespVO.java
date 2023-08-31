package com.starcloud.ops.business.app.api.limit.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppPublishLimitRespVO", description = "应用发布限流响应VO")
public class AppPublishLimitRespVO implements Serializable {

    private static final long serialVersionUID = 5666285914382396831L;

    /**
     * UID
     */
    @Schema(description = "UID")
    private String uid;

    /**
     * 发布 UID
     */
    @Schema(description = "应用 UID")
    private String appUid;

    /**
     * 发布 UID
     */
    @Schema(description = " 应用发布 UID")
    private String publishUid;

    /**
     * 发布渠道UID
     */
    @Schema(description = "应用发布渠道 UID")
    private String channelUid;

    /**
     * 频率限制配置
     */
    @Schema(description = "频率限制配置")
    private AppLimitConfigDTO rateConfig;

    /**
     * 用户用量限制配置
     */
    @Schema(description = "用户使用频率限制配置")
    private AppLimitConfigDTO userRateConfig;

    /**
     * 广告位限制配置
     */
    @Schema(description = "广告位限制配置")
    private AppLimitConfigDTO advertisingConfig;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updater;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

}
