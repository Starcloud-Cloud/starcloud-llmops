package com.starcloud.ops.business.mission.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "任务分页查询")
public class SinglePageQueryReqVO extends PageParam {

    @Schema(description = "通告Uid")
    @NotBlank(message = "通告uid不能为空")
    private String notificationUid;

    @Schema(description = "任务状态")
    @InEnum(value = SingleMissionStatusEnum.class, field = InEnum.EnumField.CODE, message = "任务状态[{value}]必须是: {values}")
    private String status;

    @Schema(description = "认领人")
    private String claimUsername;

    @Schema(description = "认领人Id")
    private String claimUserId;



}
