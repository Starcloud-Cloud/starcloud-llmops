package com.starcloud.ops.business.product.api.spu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 魔法 AI 权益赠送 VO
 */
@Data
public class GiveRightsDTO {
    /**
     * 赠送魔法豆
     */
    @Schema(description = "赠送魔法豆", example = "999")
    private Integer giveMagicBean;

    /**
     * 赠送图片权益
     */
    @Schema(description = "赠送图片权益", example = "100")
    private Integer giveImage;

    /**
     * 用户等级
     */
    @Schema(description = "用户等级", example = "1")
    private Integer level;
}