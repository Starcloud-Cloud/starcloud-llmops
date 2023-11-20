package com.starcloud.ops.business.mission.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询")
public class NotificationPageQueryReqVO extends PageParam {
}
