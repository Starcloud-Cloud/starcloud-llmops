package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import com.starcloud.ops.business.dataset.pojo.dto.CleanRuleVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @className    : DatasetsBaseVO
 * @description  : [数据集 Base VO，提供给添加、修改、详细的子 VO 使用]
 * @author       : [wuruiqiang]
 * @version      : [v1.0]
 * @createTime   : [2023/5/31 16:02]
 * @updateUser   : [AlanCusack]
 * @updateTime   : [2023/5/31 16:02]
 * @updateRemark : [暂无修改]
 */
@Data
public class DatasetHandleRulesBaseVO {

    @Schema(description = "数据集ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据集ID不能为空")
    private Long datasetId;

    @Schema(description = "预处理规则", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预处理规则不能为空")
    private CleanRuleVO CleanRuleVO;

    @Schema(description = "分段规则", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分段规则不能为空")
    private SplitRule splitRule;

}