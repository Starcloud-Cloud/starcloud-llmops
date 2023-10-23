package com.starcloud.ops.business.listing.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询词库")
public class DictPageReqVO extends PageParam {

    @Schema(description = "词库名称")
    private String name;

}
