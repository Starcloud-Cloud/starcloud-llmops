package com.starcloud.ops.business.app.api.limit.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "AppPublishLimitReqVO", description = "应用发布限流基础请求VO")
public class AppPublishLimitReqVO implements Serializable {

    private static final long serialVersionUID = 5666285914382396831L;

    /**
     * 发布 UID
     */
    @Schema(description = "应用 UID")
    @NotBlank(message = "应用发布限流中：应用UID 不能为空")
    private String appUid;

    /**
     * 发布 UID
     */
    @Schema(description = " 应用发布 UID")
    @NotBlank(message = "应用发布限流中：发布UID 不能为空")
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
    @Valid
    private AppLimitConfigReqVO rateConfig;

    /**
     * 用户用量限制配置
     */
    @Schema(description = "用户使用频率限流配置")
    @Valid
    private AppLimitConfigReqVO userRateConfig;

    /**
     * 广告位限制配置
     */
    @Schema(description = "广告位限制配置")
    @Valid
    private AppLimitConfigReqVO advertisingConfig;

}
