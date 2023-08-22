package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据集 规则更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetHandleRulesUpdateReqVO extends DatasetHandleRulesBaseVO {

    @Schema(description = "规则编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规则编号不能为空")
    private Long id;

}