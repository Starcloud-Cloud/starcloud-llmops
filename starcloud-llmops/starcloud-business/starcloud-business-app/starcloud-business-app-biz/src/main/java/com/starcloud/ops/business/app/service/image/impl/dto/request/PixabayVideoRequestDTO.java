package com.starcloud.ops.business.app.service.image.impl.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Pixabay 图片请求 DTO
 * PixabayImageRequestDTO
 */
@Data
public class PixabayVideoRequestDTO extends PixabayCommonRequestDTO {

    @Schema(description = "视频类型")
    @JsonProperty(value = "video_type")
    private String videoType;
}
