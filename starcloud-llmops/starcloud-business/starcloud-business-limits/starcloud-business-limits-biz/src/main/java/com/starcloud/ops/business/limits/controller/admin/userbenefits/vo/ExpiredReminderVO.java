package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 用户权益余量信息 VO")
@Data
@ToString(callSuper = true)
public class ExpiredReminderVO {




    private UserBenefits userLevel;

    private UserBenefits userBenefits;

}
