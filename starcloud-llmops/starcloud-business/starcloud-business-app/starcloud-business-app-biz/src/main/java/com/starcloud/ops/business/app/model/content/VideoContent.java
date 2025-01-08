package com.starcloud.ops.business.app.model.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VideoContent {
//
//    @Schema(description = "图片模板code 等于ImageContent.code")
//    private String code;
//
//    @Schema(description = "图片模板名称 等于ImageContent.name")
//    private String name;
//
//    @Schema(description = "图片序号 等于ImageContent.index")
//    private Integer index;

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

//    @Schema(description = "异常信息")
//    private String msg;


}
