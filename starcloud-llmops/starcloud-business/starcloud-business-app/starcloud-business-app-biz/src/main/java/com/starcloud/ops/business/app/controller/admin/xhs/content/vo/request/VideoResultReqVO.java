package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "查询视频生成结果")
public class VideoResultReqVO {


    @Schema(description = "视频uid")
    @NotBlank(message = "视频uid必填")
    private String videoUid;

    @Schema(description = "创作内容uid")
    @NotBlank(message = "创作内容uid必填")
    private String creativeContentUid;

    @Schema(description = "图片模板code")
    @NotBlank(message = "图片模板code必填")
    private String imageCode;
}
