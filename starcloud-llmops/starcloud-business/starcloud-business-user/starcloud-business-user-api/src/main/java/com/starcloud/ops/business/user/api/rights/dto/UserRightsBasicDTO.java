package com.starcloud.ops.business.user.api.rights.dto;


import com.starcloud.ops.business.user.api.level.dto.OperateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
     * 赠送图片权益
     */
    @Schema(description = "模板数量", example = " 1")
    private Integer template;

    /**
     * 时间范围
     */
    @Schema(description = "时间范围", example = " 1")
    private TimesRangeDTO timesRange;

    /**
     * 操作配置 DTO
     */
    @Schema(description = "操作配置")
    private OperateDTO operateDTO;


}
