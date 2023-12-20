package com.starcloud.ops.business.mission.api.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "小程序查询通告任务列表")
public class AppMissionQueryReqVO extends PageParam {

    @Schema(description = "通告uid")
    @NotBlank(message = "通告uid 不能为空")
    private String notificationUid;

}
