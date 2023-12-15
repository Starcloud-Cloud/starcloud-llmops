package com.starcloud.ops.business.product.controller.admin.spu.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.starcloud.ops.business.user.enums.level.AdminUserLevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 魔法 AI 权益赠送 VO
 */
@Data
public class GiveRightsVO {
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
    @InEnum(value = AdminUserLevelEnum.class, message = "用户等级所设置值，必须是 {value}")
    private Integer level;
}