package com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 用户权益使用日志更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBenefitsUsageLogUpdateReqVO extends UserBenefitsUsageLogBaseVO {

    @Schema(description = "主键ID", required = true)
    @NotNull(message = "主键ID不能为空")
    private Long id;

}
