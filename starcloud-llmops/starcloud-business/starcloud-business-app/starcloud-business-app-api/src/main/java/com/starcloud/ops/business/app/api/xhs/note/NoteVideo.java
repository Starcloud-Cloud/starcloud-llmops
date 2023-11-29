package com.starcloud.ops.business.app.api.xhs.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "视频")
public class NoteVideo {

    @Schema(description = "视频详情")
    private VideoMedia media;

    private VideoImage image;
}
