package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.simpleframework.xml.Default;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 上传基础 Request VO")
@Data
public class UploadReqVO {


    @Schema(description = "应用 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "应用 ID不可以为空")
    private String appId;

    @Schema(description = " 会话 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sessionId;

    @Schema(description = "上传批次", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = " 上传批次不可以为空")
    private String batch;

    @Schema(description = "操作类型 -文档/QA", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dataModel;

    @Schema(description = "数据类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataType;

    @Schema(description = "分块是否同步", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean splitSync;

    @Schema(description = "清洗是否同步", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean cleanSync;

    @Schema(description = "索引是否同步", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean indexSync;

    @Schema(description = "是否生成总结")
    private Boolean enableSummary = false;

}