package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import com.starcloud.ops.business.dataset.pojo.dto.CleanRuleVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规则名称不能为空")
    @Size(max = 100, message = "规则名称不能超过 100 个字符")
    private String ruleName;

    @Schema(description = "过滤规则组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "过滤规则组不能为空")
    private List<String> ruleFilter;

    @Schema(description = "预处理规则", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预处理规则不能为空")
    private CleanRuleVO cleanRule;

    @Schema(description = "分段规则", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分段规则不能为空")
    private SplitRule splitRule;

    @Schema(description = "规则类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规则类型不能为空")
    private String ruleType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规则状态不能为空")
    private Boolean enable;

    @Schema(description = "数据集UID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据集UID不能为空")
    private String datasetUid;

    @Schema(description = "规则来源", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fromScene;
}