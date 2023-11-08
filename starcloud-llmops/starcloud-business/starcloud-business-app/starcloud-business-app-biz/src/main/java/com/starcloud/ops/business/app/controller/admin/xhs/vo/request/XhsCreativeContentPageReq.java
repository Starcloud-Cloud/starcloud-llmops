package com.starcloud.ops.business.app.controller.admin.xhs.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询创作内容")
public class XhsCreativeContentPageReq extends PageParam {

    @Schema(description = "创作计划Uid")
    private String planUid;
}
