package com.starcloud.ops.business.user.api.rights.dto;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 添加用户权益DTO类
 */
@Data
public class AddRightsDTO {

    @Schema(description = "用户 ID", example = "1")
    @NotNull(message = "用户 ID不能为空")
    private Long userId;

    @Schema(description = "用户 ID", example = "1")
    @Min(value = 0, message = "添加魔法豆数量不能小于 0")
    private Integer magicBean;

    @Schema(description = "赠送魔法图片权益", example = "100")
    @Min(value = 0, message = "添加图片权益数量不能小于 0")
    private Integer magicImage;

    @Schema(description = "矩阵豆", example = " 1")
    @Min(value = 0, message = "添加矩阵豆数量不能小于 0")
    private Integer matrixBean;


    @Schema(description = "模板", example = " 1")
    @Min(value = 0, message = "添加矩阵豆数量不能小于 0")
    private Integer template;

    @Schema(description = "生效时间数", example = "1")
    private Integer timeNums;

    @Schema(description = "生效时间单位", example = "1")
    @InEnum(value = TimeRangeTypeEnum.class, message = "用户等级生效时间单位，必须是 {value}")
    private Integer timeRange;

    @Schema(description = "业务类型", example = "1")
    @InEnum(value = AdminUserRightsBizTypeEnum.class, message = "业务类型不存在 {value} 请选择合适的业务类型")
    Integer bizType;

    @Schema(description = "业务 ID", example = "1")
    @NotNull(message = "业务 ID不能为空")
    private String bizId;

    @Schema(description = "用户等级 ID", example = "1")
    private Long levelId;
}
