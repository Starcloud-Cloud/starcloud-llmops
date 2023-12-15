package com.starcloud.ops.business.mission.api.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "小程序认领任务")
public class AppClaimReqVO {

    @Schema(description = "通告uid")
    @NotBlank(message = "通告uid不能为空")
    private String notificationUid;

    @Schema(description = "认领人id")
    @NotBlank(message = "认领人id不能为空")
    private String claimUserId;

    @Schema(description = "认领人")
    @NotBlank(message = "认领人不能为空")
    private String claimUsername;
}
