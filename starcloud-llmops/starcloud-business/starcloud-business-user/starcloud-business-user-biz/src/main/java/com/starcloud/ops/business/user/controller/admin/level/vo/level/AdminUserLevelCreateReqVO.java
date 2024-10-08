package com.starcloud.ops.business.user.controller.admin.level.vo.level;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会员等级创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminUserLevelCreateReqVO extends AdminUserLevelBaseVO {


    @Schema(description = "会员等级 预设开始时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime startTime;


    @Schema(description = "会员等级 预设结束时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime endTime;


    @Schema(description = "时间数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "时间数不能为空")
    private Integer timeNums;

    @Schema(description = "时间范围类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "时间范围类型不能为空")
    @InEnum(value = TimeRangeTypeEnum.class, message = "时间范围类型[{value}]必须是: {values}")
    private Integer timeRange;

}
