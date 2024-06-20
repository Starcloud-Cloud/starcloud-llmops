package com.starcloud.ops.business.app.controller.admin.appinfrajob.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 应用定时执行任务分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppInfraJobPageReqVO extends PageParam {

    @Schema(description = "任务名称", example = "李四")
    private String name;

    @Schema(description = "应用来源")
    private Integer appFrom;

    @Schema(description = "应用编号", example = "14120")
    private String creativePlanUid;

    @Schema(description = "CRON 表达式")
    private String cronExpression;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "任务状态", example = "2")
    private Integer status;

}