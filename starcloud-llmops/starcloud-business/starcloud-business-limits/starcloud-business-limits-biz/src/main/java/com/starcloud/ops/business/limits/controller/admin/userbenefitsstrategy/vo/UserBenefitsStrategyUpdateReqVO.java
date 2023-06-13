package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo;


import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 用户权益策略表 更新 Request VO")
@Data
@ToString(callSuper = true)
public class UserBenefitsStrategyUpdateReqVO {

    @Schema(description = " 主键 ID")
    private Long id;

    @Schema(description = "兑换码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 12, message = "兑换码长度不能超过12个字符")
    private String code;

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30, message = "策略名称长度不能超过30个字符")
    private String strategyName;

    @Schema(description = "策略描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 100, message = "策略描述长度不能超过100个字符")
    private String strategyDesc;

    @Schema(description = "应用数")
    private Long appCount;

    @Schema(description = "数据集数")
    private Long datasetCount;

    @Schema(description = "图片数")
    private Long imageCount;

    @Schema(description = "令牌数")
    private Long tokenCount;

    @Schema(description = "限制兑换次数（-1，不设限制）")
    private Long limitUnit;

}
