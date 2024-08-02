package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class SliceCountReqVO {

    @Schema(description = "素材编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private Long sliceId;

    @Schema(description = "使用次数",defaultValue = "1")
    private Integer nums = 1;

}