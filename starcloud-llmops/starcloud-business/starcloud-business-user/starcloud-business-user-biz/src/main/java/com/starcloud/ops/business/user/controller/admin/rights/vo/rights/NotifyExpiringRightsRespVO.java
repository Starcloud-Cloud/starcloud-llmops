package com.starcloud.ops.business.user.controller.admin.rights.vo.rights;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "系统会员用户 App - 用户权益过期提醒 Response VO")
@Data
public class NotifyExpiringRightsRespVO {

    @Schema(description = "过期名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = " 权益类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rightsType;

    @Schema(description = "是否开启提醒", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isNotify;

    @Schema(description = "7 天内过期的数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer ExpiredNum;
}
