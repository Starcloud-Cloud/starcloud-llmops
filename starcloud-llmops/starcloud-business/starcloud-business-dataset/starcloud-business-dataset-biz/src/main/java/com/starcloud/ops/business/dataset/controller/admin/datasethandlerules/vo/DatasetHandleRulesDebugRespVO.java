package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 数据集规则 Response VO")
@Data
@ToString(callSuper = true)
public class DatasetHandleRulesDebugRespVO {

    @Schema(description = "命中规则名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ruleName;

    @Schema(description = "清洗后数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private String data;
}
