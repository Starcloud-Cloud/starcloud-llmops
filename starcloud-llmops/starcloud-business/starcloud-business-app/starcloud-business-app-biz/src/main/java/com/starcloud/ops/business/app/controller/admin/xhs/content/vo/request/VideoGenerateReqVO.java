package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "视频生成请求参数")
public class VideoGenerateReqVO {

    @Schema(description = "创作内容uid")
    @NotBlank(message = "创作内容uid必填")
    private String creativeContentUid;

    @Schema(description = "快捷配置")
    private String quickConfiguration;

    @Schema(description = "失败重试")
    private Boolean retry;
}
