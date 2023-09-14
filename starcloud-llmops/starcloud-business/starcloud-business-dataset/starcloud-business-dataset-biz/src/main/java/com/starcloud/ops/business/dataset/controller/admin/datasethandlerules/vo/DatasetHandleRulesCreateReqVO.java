package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据集规则 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetHandleRulesCreateReqVO extends DatasetHandleRulesBaseVO {

}
