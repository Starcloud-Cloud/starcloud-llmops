package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据集源数据更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetSourceDataUpdateReqVO extends DatasetSourceDataBaseVO {

    @Schema(description = "主键ID", required = true, example = "4784")
    @NotNull(message = "主键ID不能为空")
    private Long id;

}