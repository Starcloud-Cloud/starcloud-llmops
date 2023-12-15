package com.starcloud.ops.business.mission.api.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "小程序查询通告列表")
public class AppNotificationQueryReqVO extends PageParam {

    @Schema(description = "用户id")
    @NotBlank(message = "用户id不能为空")
    private String claimUserId;

    @Schema(description = "是否公开")
    private Boolean open;

    @Schema(description = "任务创建人")
    private List<String> creator;

}
