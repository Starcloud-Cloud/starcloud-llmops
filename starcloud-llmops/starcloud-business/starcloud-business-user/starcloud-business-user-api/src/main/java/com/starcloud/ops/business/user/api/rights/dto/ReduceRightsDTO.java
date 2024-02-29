package com.starcloud.ops.business.user.api.rights.dto;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 权益扣除的 DTO
 */
@Data
public class ReduceRightsDTO {

    @Schema(description = "用户 ID", example = "1")
    @NotNull(message = "用户 ID不能为空")
    private Long userId;

    @Schema(description = "团队所属人", example = "1")
    @NotNull(message = "团队所属人ID不能为空")
    private Long teamOwnerId;

    @Schema(description = "团队 ID", example = "1")
    @NotNull(message = "团队ID不能为空")
    private Long teamId;

    @Schema(description = "权益类型", example = "1")
    @InEnum(value = AdminUserRightsTypeEnum.class, message = "权益类型不符，必须是 {value}")
    private Integer rightType;

    @Schema(description = "扣除的权益数量", example = "1")
    @Min(value = 1L, message = "扣除的权益数量必须大于 0")
    private Integer reduceNums;

    @Schema(description = "业务类型", example = "1")
    @InEnum(value = AdminUserRightsBizTypeEnum.class, message = "业务类型不存在 {value} 请选择合适的业务类型")
    private Integer bizType;
    @Schema(description = "业务 ID", example = "1")
    private String bizId;
}
