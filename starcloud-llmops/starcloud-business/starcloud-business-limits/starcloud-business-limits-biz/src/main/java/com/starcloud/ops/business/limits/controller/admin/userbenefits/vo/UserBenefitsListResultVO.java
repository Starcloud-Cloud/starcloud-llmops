package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 用户权益余量信息 VO")
@Data
@ToString(callSuper = true)
@AllArgsConstructor
public class UserBenefitsListResultVO {


    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "数量")
    private Long amount;

}
