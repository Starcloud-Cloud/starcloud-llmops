package com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 会员等级创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminUserLevelConfigCreateReqVO extends AdminUserLevelConfigBaseVO {

}
