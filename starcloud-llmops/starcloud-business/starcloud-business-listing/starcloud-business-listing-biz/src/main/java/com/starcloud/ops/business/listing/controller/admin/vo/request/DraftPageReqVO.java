package com.starcloud.ops.business.listing.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询词库")
public class DraftPageReqVO extends PageParam {

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "正序")
    private Boolean asc;

}
