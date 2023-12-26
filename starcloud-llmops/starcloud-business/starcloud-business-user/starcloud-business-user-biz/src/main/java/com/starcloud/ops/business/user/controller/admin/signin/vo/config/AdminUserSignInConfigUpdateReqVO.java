package com.starcloud.ops.business.user.controller.admin.signin.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 签到规则更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminUserSignInConfigUpdateReqVO extends AdminUserSignInConfigBaseVO {

    @Schema(description = "规则自增主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13653")
    @NotNull(message = "规则自增主键不能为空")
    private Long id;

}
