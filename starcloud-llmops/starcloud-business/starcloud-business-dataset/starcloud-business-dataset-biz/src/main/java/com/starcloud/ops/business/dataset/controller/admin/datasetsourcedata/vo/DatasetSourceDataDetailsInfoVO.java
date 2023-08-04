package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 数据集源数据 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class DatasetSourceDataDetailsInfoVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "编号",  requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号不能为空")
    private String uid;

    @Schema(description = "名称", required = true)
    @NotNull(message = "名称不能为空")
    private String name;

    @Schema(description = "总结内容")
    private String summaryContent;

    @Schema(description = "批次", required = true)
    @NotNull(message = "批次不能为空")
    private String batch;

    @Schema(description = "字数")
    private Long wordCount;

    @Schema(description = "令牌数")
    private Long tokens;

    @Schema(description = " 状态")
    private Long status;


}