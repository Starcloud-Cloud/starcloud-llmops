package com.starcloud.ops.business.app.controller.admin.opus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "绑定创作内容")
public class OpusBindBaseVO {

    @Schema(description = "作品集uid")
    @NotBlank(message = "作品集uid必填")
    private String opusUid;

    @Schema(description = "目录uid")
    @NotBlank(message = "目录uid必填")
    private String dirUid;

    @Schema(description = "创作内容uid")
    @NotBlank(message = "创作内容uid必填")
    private String creativeContentUid;

    @Schema(description = "视频")
    private Boolean video;

    @Schema(description = "开启")
    private Boolean open;
}
