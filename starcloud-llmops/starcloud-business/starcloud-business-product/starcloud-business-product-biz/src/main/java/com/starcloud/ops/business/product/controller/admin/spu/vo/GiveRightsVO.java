package com.starcloud.ops.business.product.controller.admin.spu.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
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
     * 权益生效时间
     */
    @Schema(description = "权益生效时间", example = "100")
    private Integer rightsTimeNums;

    /**
     * 权益生效时间单位
     */
    @Schema(description = "权益生效时间单位", example = "100")
    @InEnum(value = TimeRangeTypeEnum.class, message = "权益生效时间单位，必须是 {value}")
    private Integer rightsTimeRange;

    /**
     * 用户等级
     */
    @Schema(description = "用户等级", example = "1")
    private Long levelId;

    @Schema(description = "用户等级生效时间", example = "100")
    private Integer levelTimeNums;

    @Schema(description = "用户等级生效时间单位", example = "100")
    @InEnum(value = TimeRangeTypeEnum.class, message = "用户等级生效时间单位，必须是 {value}")
    private Integer LevelTimeRange;

}
