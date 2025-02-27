package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response;

import com.starcloud.ops.business.app.model.content.VideoContentInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "视频生成结果")
public class VideoResult {

    @Schema(description = "完成")
    private boolean finished;

    @Schema(description = "结果")
    private VideoContentInfo video;

}
