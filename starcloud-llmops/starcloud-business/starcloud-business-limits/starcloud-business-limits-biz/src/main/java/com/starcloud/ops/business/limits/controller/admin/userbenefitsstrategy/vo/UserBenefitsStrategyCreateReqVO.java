package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Size;


@Schema(description = "星河云海 - 用户权益策略表 创建 Request VO")
@Data
@ToString(callSuper = true)
public class UserBenefitsStrategyCreateReqVO {

    @Schema(description = "兑换码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 12, message = "兑换码长度不能超过12个字符")
    private String code;

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30, message = "策略名称长度不能超过30个字符")
    private String strategyName;

    @Schema(description = "策略描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 100, message = "策略描述长度不能超过100个字符")
    private String strategyDesc;

    @Schema(description = "策略类型（字典中管理）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String strategyType;

    @Schema(description = "应用数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long appCount;

    @Schema(description = "数据集数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long datasetCount;

    @Schema(description = "图片数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long imageCount;

    @Schema(description = "令牌数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tokenCount;

    @Schema(description = "适用范围（字典中管理）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String scope;

    @Schema(description = "适用时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer scopeNum;

    @Schema(description = "限制兑换次数（-1，不设限制）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long limitUnit;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;
}