package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户权益余量信息 VO")
@Data
@ToString(callSuper = true)
public class UserBenefitsInfoResultVO {


    @Schema(description = "应用数剩余数")
    private Long appCountUsed;

    @Schema(description = "数据集数")
    private Long datasetCountUsed;

    @Schema(description = "图片数")
    private Long imageCountUsed;

    @Schema(description = "令牌数")
    private Long tokenCountUsed;

    @Schema(description = "应用数总数")
    private Long appTotal;

    @Schema(description = "数据集总数")
    private Long datasetTotal;

    @Schema(description = "图片总数")
    private Long imageTotal;

    @Schema(description = "令牌总数")
    private Long tokenTotal;

    @Schema(description = "查询时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime queryTime;

}
