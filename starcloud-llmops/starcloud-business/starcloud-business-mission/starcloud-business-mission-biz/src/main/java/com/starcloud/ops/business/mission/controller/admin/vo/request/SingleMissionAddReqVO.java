package com.starcloud.ops.business.mission.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "增加单条任务")
public class SingleMissionAddReqVO {

    @Schema(description = "通告Uid")
    private String notificationUid;

    @Schema(description = "创作任务Uid")
    private List<String> creativeUids;
}
