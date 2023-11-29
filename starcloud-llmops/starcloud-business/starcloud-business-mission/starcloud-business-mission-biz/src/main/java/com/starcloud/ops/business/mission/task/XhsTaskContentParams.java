package com.starcloud.ops.business.mission.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class XhsTaskContentParams {

    @Schema(description = "任务类型")
    private String singleMissionType;

    @Schema(description = "查询数量")
    private Integer limitSize;

    @Schema(description = "失败重试")
    private Boolean failRetry;

}
