package com.starcloud.ops.business.mission.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "任务分页查询")
public class SinglePageQueryReqVO extends PageParam {

    @Schema(description = "通告Uid")
    @NotBlank(message = "通告uid不能为空")
    private String notificationUid;

}
