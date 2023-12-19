package com.starcloud.ops.business.mission.api.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "已认领任务查询")
public class ClaimedMissionQueryReqVO extends PageParam {

    @Schema(description = "认领人id")
    @NotBlank(message = "认领人id不能为空")
    private String claimUserId;

    @Schema(description = "任务状态")
    @InEnum(value = SingleMissionStatusEnum.class, field = InEnum.EnumField.CODE, message = "平台类型[{value}]必须是: {values}")
    private String singleMissionStatus;
}
