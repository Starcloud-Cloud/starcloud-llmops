package com.starcloud.ops.business.mission.api.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "已认领任务查询")
public class ClaimedMissionQueryReqVO extends PageParam {

    @Schema(description = "认领人uid")
    @NotBlank(message = "认领人uid不能为空")
    private Long claimUserId;
}
