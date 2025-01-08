package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询视频生成结果")
public class VideoResultReqVO {


    @Schema(description = "视频uid")
    private String videoUid;

    @Schema(description = "创作内容uid")
    private String creativeContentUid;
}
