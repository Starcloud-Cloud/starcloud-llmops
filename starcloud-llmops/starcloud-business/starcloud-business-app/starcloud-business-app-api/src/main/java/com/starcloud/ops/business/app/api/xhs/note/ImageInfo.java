package com.starcloud.ops.business.app.api.xhs.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "单个图片")
public class ImageInfo {

    @Schema(description = "图片类型")
    private String imageScene;

    @Schema(description = "图片链接")
    private String url;

}
