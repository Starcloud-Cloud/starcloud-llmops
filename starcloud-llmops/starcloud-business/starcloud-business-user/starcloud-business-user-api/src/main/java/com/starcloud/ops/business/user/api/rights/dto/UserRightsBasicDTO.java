package com.starcloud.ops.business.user.api.rights.dto;


import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRightsBasicDTO {

    /**
     * 赠送魔法豆
     */
    @Schema(description = "赠送魔法豆", example = "999")
    private Integer magicBean;

    /**
     * 赠送图片权益
     */
    @Schema(description = "赠送图片权益", example = "100")
    private Integer magicImage;

    /**
     * 赠送图片权益
     */
    @Schema(description = "矩阵点", example = " 1")
    private Integer matrixBean;

    /**
     * 时间范围
     */
    @Schema(description = "时间范围", example = " 1")
    private TimesRangeDTO timesRange;

}
