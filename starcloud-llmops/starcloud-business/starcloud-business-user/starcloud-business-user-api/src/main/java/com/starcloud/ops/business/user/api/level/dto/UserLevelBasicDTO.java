package com.starcloud.ops.business.user.api.level.dto;


import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.user.api.rights.dto.TimesRangeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserLevelBasicDTO {

    /**
     * 用户等级
     */
    @Schema(description = "用户等级", example = "1")
    private Long levelId;

    /**
     * 时间范围
     */
    @Schema(description = "时间范围", example = " 1")
    private TimesRangeDTO timesRange;
}
