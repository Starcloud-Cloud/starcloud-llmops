package com.starcloud.ops.business.app.service.image.impl.dto.request;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pixabay 图片请求 DTO
 * PixabayImageRequestDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixabayImageRequestDTO extends PixabayCommonRequestDTO {

    /**
     * Filter results by image type.
     * Accepted values: "all", "photo", "illustration", "vector"
     * Default: "all"
     * <br/>
     * 按图像类型过滤结果。
     * 接受值:"all"， "photo"， "illustration"，"vector"
     * 默认值:“all”
     */
    @Schema(description = "图像类型")
    @JsonProperty(value = "image_type")
    private String imageType;

    /**
     * 图像是宽于高，还是高于宽。
     * 可接受值:"all"， "horizontal"， "vertical"
     * 默认值:“all”
     */
    @Schema(description = "图像是宽于高，还是高于宽", defaultValue = "all")
    private String orientation;

    /**
     * Filter images by color properties.  A comma separated list of values may be used to select multiple properties.
     * Accepted values: "grayscale", "transparent", "red", "orange", "yellow", "green", "turquoise", "blue", "lilac", "pink", "white", "gray", "black", "brown" <br/>
     * 过滤图像的颜色属性。可以使用逗号分隔的值列表来选择多个属性。
     * 可接受的值:“灰度”、“透明”、“红色”、“橙色”、“黄色”、“绿色”、“绿松石色”、“蓝色”、“丁香色”、“粉红色”、“白色”、“灰色”、“黑色”、“棕色”
     */
    @Schema(description = "过滤图像的颜色属性")
    private String colors;

    public PixabayImageRequestDTO(PixabayImageRequestDTO requestDTO) {
        BeanUtil.toBean(requestDTO, PixabayImageRequestDTO.class);
    }

}