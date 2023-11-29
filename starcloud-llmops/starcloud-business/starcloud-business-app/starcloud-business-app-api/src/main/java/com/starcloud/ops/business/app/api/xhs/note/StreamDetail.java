package com.starcloud.ops.business.app.api.xhs.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "单个视频详情")
public class StreamDetail {

    @Schema(description = "视频url")
    private String masterUrl;

    @Schema(description = "视频清晰度类型")
    private String qualityType;

    @Schema(description = "视频比特率")
    private Long avgBitrate;

    @Schema(description = "视频格式")
    private String format;

    @Schema(description = "备份url")
    private List<String> backupUrls;
}
