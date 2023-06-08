package com.starcloud.ops.business.dataset.controller.admin.datasets.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据集更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetsUpdateReqVO extends DatasetsBaseVO {

    @Schema(description = "数据集编号", required = true)
    @NotNull(message = "数据集编号不能为空")
    private String uid;

}