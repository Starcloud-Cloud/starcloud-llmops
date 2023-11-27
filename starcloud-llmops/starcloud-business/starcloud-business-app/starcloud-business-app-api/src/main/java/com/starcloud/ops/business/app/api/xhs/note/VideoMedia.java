package com.starcloud.ops.business.app.api.xhs.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "视频详情")
public class VideoMedia {

    @Schema(description = "视频id")
    private String videoId;

    private MediaVideo video;

    @Schema(description = "视频详情")
    private MediaStream stream;
}
