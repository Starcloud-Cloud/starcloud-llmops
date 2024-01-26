package com.starcloud.ops.business.mission.api.vo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "小程序查询通告详情")
public class AppNotificationDetailReqVO {

    @Schema(description = "通告id")
    @NotNull(message = "通告id不能为空")
    private Long id;

    @Schema(description = "邀请人id")
    private Long inviteUser;
}
