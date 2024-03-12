package com.starcloud.ops.business.user.controller.admin.level.vo.level;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 会员等级权益使用结果 Response VO")
@Data
@ToString(callSuper = true)
public class AdminUserLevelLimitRespVO {

    @Schema(description = "是否可以通过", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean pass;

    @Schema(description = "已经使用次数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer usedCount;

}
