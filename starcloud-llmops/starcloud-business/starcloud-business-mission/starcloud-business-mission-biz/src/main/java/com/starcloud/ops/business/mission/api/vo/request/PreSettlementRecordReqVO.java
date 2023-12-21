package com.starcloud.ops.business.mission.api.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "预结算记录查询")
public class PreSettlementRecordReqVO extends PageParam {

    @Schema(description = "任务uid")
    @NotBlank(message = "任务uid 不能为空")
    private String missionUid;

//    @Schema(description = "认领人id")
//    @NotBlank(message = "认领人id不能为空")
//    private String claimUserId;
}
