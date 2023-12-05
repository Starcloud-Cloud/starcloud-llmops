package com.starcloud.ops.business.dto;

import com.starcloud.ops.business.app.api.xhs.content.dto.XhsCreativePictureContentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "发帖内容")
public class PostingContentDTO {

    @Schema(description = "发帖标题")
    private String title;

    @Schema(description = "发帖内容")
    private String text;

    @Schema(description = "发帖图片")
    private List<XhsCreativePictureContentDTO> picture;
}
