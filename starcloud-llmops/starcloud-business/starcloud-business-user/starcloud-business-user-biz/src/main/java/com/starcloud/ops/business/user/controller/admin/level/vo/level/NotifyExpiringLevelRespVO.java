package com.starcloud.ops.business.user.controller.admin.level.vo.level;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "系统会员用户 App - 用户等级过期提醒 Response VO")
@Data
public class NotifyExpiringLevelRespVO {

    @Schema(description = "会员等级", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long levelId;

    @Schema(description = " 等级名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String levelName;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime validEndTime;

    @Schema(description = "是否开启提醒", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isNotify;
}
