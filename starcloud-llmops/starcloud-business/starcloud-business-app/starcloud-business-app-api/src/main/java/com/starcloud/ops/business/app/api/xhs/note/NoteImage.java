package com.starcloud.ops.business.app.api.xhs.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "图片")
public class NoteImage {

    private String traceId;

    private String url;

    @Schema(description = "图片宽度")
    private Integer width;

    @Schema(description = "图片高度")
    private Integer height;

    private String fileId;

    @Schema(description = "图片集合")
    private List<ImageInfo> infoList;
}
