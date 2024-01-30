package com.starcloud.ops.business.user.controller.admin.invite.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
* 邀请规则新增 ReqVO，提供给添加使用
*/
@Schema(description = "管理后台 - 邀请规则创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminUserInviteRuleCreateReqVO extends AdminUserInviteRuleBaseVO{

}
