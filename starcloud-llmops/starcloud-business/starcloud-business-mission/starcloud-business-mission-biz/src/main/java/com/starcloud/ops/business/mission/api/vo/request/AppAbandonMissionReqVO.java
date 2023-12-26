package com.starcloud.ops.business.mission.api.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "放弃任务")
public class AppAbandonMissionReqVO {

    @Schema(description = "任务uid")
    @NotBlank(message = "任务uid不能为空")
    private String missionUid;

//    @Schema(description = "认领人uid")
//    @NotBlank(message = "认领人uid不能为空")
//    private String claimUserId;
}
