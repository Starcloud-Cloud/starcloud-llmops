package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 数据集规则 Response VO")
@Data
@ToString(callSuper = true)
public class HandleRuleProcessResultRespVO {

    @Schema(description = "规则ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ruleId;

    @Schema(description = "转换的格式", requiredMode = Schema.RequiredMode.REQUIRED)
    private String convertFormat;

    @Schema(description = "转换的格式", requiredMode = Schema.RequiredMode.REQUIRED)
    private String formatSuffix;

    @Schema(description = "分段规则", requiredMode = Schema.RequiredMode.REQUIRED)
    private SplitRule splitRule;

    @Schema(description = "清洗后数据名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resultName;

    @Schema(description = "清洗后的结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private String result;




}
