package com.starcloud.ops.business.app.api.xhs.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "生成图片")
public class XhsCreativePictureContentDTO {

    @Schema(description = "图片序号")
    private Integer index;

    /**
     * 是否主图
     */
    @Schema(description = "是否主图")
    private Boolean isMain;

    /**
     * 应用生成参数
     */
    @Schema(description = "返回数据")
    private String url;
}
