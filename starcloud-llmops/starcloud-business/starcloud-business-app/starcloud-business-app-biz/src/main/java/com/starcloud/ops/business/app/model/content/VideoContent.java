package com.starcloud.ops.business.app.model.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class VideoContent implements Serializable {

    private static final long serialVersionUID = 3222185824454704834L;

    @Schema(description = "图片模板code 等于ImageContent.code")
    private String code;

    @Schema(description = "图片地址")
    private String imageUrl;

    @Schema(description = "视频uid")
    private String videoUid;

    @Schema(description = "视频地址")
    private String videoUrl;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "进度")
    private String progress;

    @Schema(description = "阶段")
    private String stage;

    @Schema(description = "错误信息")
    private String error;

}
