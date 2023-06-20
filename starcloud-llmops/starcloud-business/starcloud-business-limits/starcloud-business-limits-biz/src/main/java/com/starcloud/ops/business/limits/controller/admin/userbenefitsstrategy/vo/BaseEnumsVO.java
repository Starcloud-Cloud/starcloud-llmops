package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * Controller 返回枚举 VO
 */
@Data
@AllArgsConstructor
public class BaseEnumsVO {

    @Schema(description = " 枚举 coed")
    private String code;

    @Schema(description = "枚举 名称")
    private String name;

}