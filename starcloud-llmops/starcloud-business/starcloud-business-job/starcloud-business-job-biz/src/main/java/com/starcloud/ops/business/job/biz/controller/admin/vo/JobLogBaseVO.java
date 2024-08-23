package com.starcloud.ops.business.job.biz.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "定时任务日志")
public class JobLogBaseVO {

    @Schema(description = "powerjob任务id")
    private Long jobId;

    @Schema(description = "任务uid")
    private String businessJobUid;

    @Schema(description = "触发类型")
    private Integer triggerType;

    @Schema(description = "触发时间")
    private LocalDateTime triggerTime;

    @Schema(description = "执行配置")
    private String executeConfig;

    @Schema(description = "执行结果")
    private String executeResult;

    @Schema(description = "耗时")
    private Long executeTime;

    @Schema(description = "是否成功")
    private Boolean success;
}
