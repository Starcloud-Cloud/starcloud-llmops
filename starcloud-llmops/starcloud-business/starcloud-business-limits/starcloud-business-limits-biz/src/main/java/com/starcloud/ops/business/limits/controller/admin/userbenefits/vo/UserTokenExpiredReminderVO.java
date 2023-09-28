package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 用户权益余量信息 VO")
@Data
@ToString(callSuper = true)
public class UserTokenExpiredReminderVO {

    @Schema(description = "过期名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "是否开启提醒", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isReminder;

    @Schema(description = " 过期数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ExpiredNum;

}
