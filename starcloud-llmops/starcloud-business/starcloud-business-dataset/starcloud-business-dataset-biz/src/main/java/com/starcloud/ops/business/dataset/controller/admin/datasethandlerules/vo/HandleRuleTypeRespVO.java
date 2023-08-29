package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 数据集规则 Response VO")
@Data
@ToString(callSuper = true)
public class HandleRuleTypeRespVO {

    @Schema(description = "规则类型名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String typeName;

    @Schema(description = "规则类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
}
