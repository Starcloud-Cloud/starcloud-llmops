package com.starcloud.ops.business.user.controller.admin.vo;

import com.starcloud.ops.business.user.controller.admin.level.vo.level.NotifyExpiringLevelRespVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.NotifyExpiringRightsRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "系统会员用户 APP - 用户过期提醒信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserNotifyExpiringRespVO {

    @Schema(description = "会员等级提醒 VO", required = true, example = "芋艿")
    private NotifyExpiringLevelRespVO notifyExpiringLevelRespVO;

    @Schema(description = "会员权益提醒VO", required = true, example = "芋艿")
    private NotifyExpiringRightsRespVO notifyExpiringRightsRespVO;
}
