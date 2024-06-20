package com.starcloud.ops.business.app.controller.admin.appinfrajob.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.util.*;

@Schema(description = "管理后台 - 应用定时执行任务新增/修改 Request VO")
@Data
public class AppInfraJobSaveReqVO {

    @Schema(description = "任务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19384")
    private Long id;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "任务名称不能为空")
    private String name;

    @Schema(description = "应用来源", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "应用来源不能为空")
    private Integer appFrom;

    @Schema(description = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14120")
    @NotNull(message = "应用编号不能为空")
    private String creativePlanUid;

    @Schema(description = "CRON 表达式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "CRON 表达式不能为空")
    private String cronExpression;

    @Schema(description = "任务状态", example = "2")
    private Integer status;

}