package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Schema(description = "星河云海 - 用户权益策略表 创建 Request VO")
@Data
@ToString(callSuper = true)
public class UserBenefitsStrategyCreateReqVO {

    @Schema(description = "兑换码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 15, message = "兑换码长度不能超过12个字符")
    private String code;

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30, message = "策略名称长度不能超过30个字符")
    private String strategyName;

    @Schema(description = "策略描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 100, message = "策略描述长度不能超过100个字符")
    private String strategyDesc;

    @Schema(description = "权益类型（字典中管理）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "权益类型（字典中管理）不能为空")
    private String strategyType;

    @Schema(description = "应用数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25436")
    @NotNull(message = "应用数不能为空")
    private Long appCount;

    @Schema(description = "数据集数", requiredMode = Schema.RequiredMode.REQUIRED, example = "9948")
    @NotNull(message = "数据集数不能为空")
    private Long datasetCount;

    @Schema(description = "图片数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25669")
    @NotNull(message = "图片数不能为空")
    private Long imageCount;

    @Schema(description = "令牌数", requiredMode = Schema.RequiredMode.REQUIRED, example = "28197")
    @NotNull(message = "令牌数不能为空")
    private Long tokenCount;

    @Schema(description = "有效时间单位范围（-1，不设限制）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "有效时间单位范围（-1，不设限制）不能为空")
    private String effectiveUnit;

    @Schema(description = "有效时间数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "有效时间数不能为空")
    private Long effectiveNum;

    @Schema(description = "限制兑换次数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "限制兑换次数不能为空")
    private Long limitNum;

    @Schema(description = "限制间隔多久可用（-1，不设限制）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "限制间隔多久可用（-1，不设限制）不能为空")
    private String limitIntervalUnit;

    @Schema(description = "限制兑换次数（-1，不设限制）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "限制兑换次数（-1，不设限制）不能为空")
    private Long limitIntervalNum;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否启用不能为空")
    private Boolean enabled;
}