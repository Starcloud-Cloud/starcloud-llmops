package com.starcloud.ops.business.poster.controller.admin.materialgroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 海报素材分组发布 Request VO")
@Data
public class MaterialGroupPublishReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6110")
    @NotEmpty(message = "素材分组编号不可以为空")
    private String uid;

    @Schema(description = "是否公开", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发布状态不能为空")
    private Boolean overtStatus = Boolean.FALSE;


}