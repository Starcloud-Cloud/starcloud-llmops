package com.starcloud.ops.business.app.api.xhs.material.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "筛选素材")
public class CreativeMaterialQueryReq {

    @Schema(description = "素材类型")
    private String typeCode;

    @Schema(description = "模糊匹配关键字")
    private String keyword;

    @Schema(description = "筛选数量")
    private Integer limitCount;

}
