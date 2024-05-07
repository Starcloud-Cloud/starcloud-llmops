package com.starcloud.ops.business.user.api.level.dto;


import com.starcloud.ops.business.user.api.rights.dto.TimesRangeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLevelBasicDTO {

    /**
     * 用户等级
     */
    @Schema(description = "用户等级", example = "1")
    private Long levelId;

    /**
     * 时间范围
     */
    @Schema(description = "时间范围")
    private TimesRangeDTO timesRange;

    /**
     * 操作配置 DTO
     */
    @Schema(description = "操作配置")
    private OperateDTO operateDTO;

}
