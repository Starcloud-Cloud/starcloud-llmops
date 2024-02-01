package com.starcloud.ops.business.user.controller.admin.invite.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 邀请规则新增 ReqVO，提供给添加使用
 */
@Schema(description = "管理后台 - 邀请规则修改 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminUserInviteRuleUpdateReqVO extends AdminUserInviteRuleBaseVO {

    @Schema(description = "规则编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "规则编号不可以为空")
    private Long id;
}
