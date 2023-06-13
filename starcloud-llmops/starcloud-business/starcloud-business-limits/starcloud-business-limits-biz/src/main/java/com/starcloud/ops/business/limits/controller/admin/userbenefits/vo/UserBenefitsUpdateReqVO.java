package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 用户权益更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBenefitsUpdateReqVO extends UserBenefitsBaseVO {

    @Schema(description = "主键ID", required = true)
    @NotNull(message = "主键ID不能为空")
    private Long id;

}
