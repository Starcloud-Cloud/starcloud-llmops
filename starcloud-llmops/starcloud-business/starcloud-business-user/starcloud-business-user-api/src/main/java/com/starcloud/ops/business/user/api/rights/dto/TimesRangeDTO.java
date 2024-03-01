package com.starcloud.ops.business.user.api.rights.dto;


import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimesRangeDTO {

    @Schema(description = "生效时间数", example = "1")
    private Integer nums;

    @Schema(description = "生效时间单位", example = "1")
    @InEnum(value = TimeRangeTypeEnum.class,message = "用户等级生效时间单位，必须是 {value}")
    private Integer range;
}
