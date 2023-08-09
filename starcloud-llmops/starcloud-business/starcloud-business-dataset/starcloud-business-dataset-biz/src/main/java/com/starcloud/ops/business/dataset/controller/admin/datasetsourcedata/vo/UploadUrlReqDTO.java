package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 字符上传 Request VO")
@Data
@ToString(callSuper = true)
public class UploadUrlReqDTO extends UploadReqVO {

    @Schema(description = "url 链接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "标题不能为空")
    private String url;
}