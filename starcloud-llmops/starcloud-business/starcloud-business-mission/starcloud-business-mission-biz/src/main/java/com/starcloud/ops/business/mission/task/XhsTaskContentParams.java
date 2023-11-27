package com.starcloud.ops.business.mission.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class XhsTaskContentParams {

    @Schema(description = "通告状态")
    private String notificationStatus;

    @Schema(description = "任务类型")
    private String singleMissionType;

    @Schema(description = "任务状态")
    private String singleMissionStatus;

    @Schema(description = "查询数量")
    private Integer limitSize;

}
