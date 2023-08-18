package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 上传基础 Request VO")
@Data
public class UploadReqVO {

    private Boolean sync;

    @Schema(description = "上传批次", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = " 上传批次不可以为空")
    private String batch;

    @Schema(description = "知识库ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = " 知识库 ID不可以为空")
    private String datasetId;

}