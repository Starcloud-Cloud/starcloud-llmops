package com.starcloud.ops.business.app.controller.admin.appinfrajob.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 应用定时执行任务 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppInfraJobRespVO {

    @Schema(description = "任务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19384")
    @ExcelProperty("任务编号")
    private Long id;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("任务名称")
    private String name;

    @Schema(description = "应用来源", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("应用来源")
    private Integer appFrom;

    @Schema(description = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14120")
    @ExcelProperty("应用编号")
    private String creativePlanUid;

    @Schema(description = "CRON 表达式", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("CRON 表达式")
    private String cronExpression;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "任务状态", example = "2")
    @ExcelProperty("任务状态")
    private Integer status;

}