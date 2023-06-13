package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户权益策略表 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBenefitsStrategyRespVO extends UserBenefitsStrategyBaseVO {

    @Schema(description = "主键ID", required = true)
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
