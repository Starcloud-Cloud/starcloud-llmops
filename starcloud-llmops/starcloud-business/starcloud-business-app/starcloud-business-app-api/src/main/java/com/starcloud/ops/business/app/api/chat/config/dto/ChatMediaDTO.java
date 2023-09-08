package com.starcloud.ops.business.app.api.chat.config.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "图片/视频")
public class ChatMediaDTO {

    @Schema(description = "1 图片 2 视频")
    private Integer mediaType;

    @Schema(description = "下载链接")
    private String url;
}
