package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Controller 返回枚举 VO
 */
@Data
@AllArgsConstructor
public class BaseEnumsVO {

    @Schema(description = " 枚举 coed")
    private String code;

    @Schema(description = "枚举 名称")
    private String name;

}