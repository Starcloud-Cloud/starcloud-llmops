package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 数据集源数据 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Schema(description = "管理后台 - 数据集源数据更新 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetSourceDataDetailRespVO extends DatasetSourceDataBaseRespVO {

    @Schema(description = "原始内容")
    private String content;

    @Schema(description = "清洗后的内容")
    private String cleanContent;

    @Schema(description = "预处理规则", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号不能为空")
    private DatasetHandleRulesRespVO ruleVO;

    @Schema(description = "存储信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "存储信息不能为空")
    private DatasetStorageBaseVO storageVO;


}