package com.starcloud.ops.business.listing.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "词库中导入关键词明细")
public class ImportKeywordReqVO {

    @Schema(description = "词库Uid")
    private String dictUid;


    // 关键词明细

}
