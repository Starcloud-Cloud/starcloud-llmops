package com.starcloud.ops.business.app.api.image.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-14
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图片元数据对象")
public class ImageMetaDTO {

    /**
     * label：属性名，用于展示
     */
    @Schema(description = "属性名，用于展示")
    private String label;

    /**
     * value：属性值，用于传递
     */
    @Schema(description = "属性值，用于传递")
    private Object value;

    /**
     * 描述，用于展示，对该选项的描述
     */
    @Schema(description = "描述，用于展示，对该选项的描述")
    private String description;

    /**
     * 图片，用于展示，对该选项的描述
     */
    @Schema(description = "图片，用于展示，对该选项的图片描述")
    private String image;

    /**
     * 图片大小比例
     */
    @Schema(description = "图片大小比例")
    private String scale;
}
