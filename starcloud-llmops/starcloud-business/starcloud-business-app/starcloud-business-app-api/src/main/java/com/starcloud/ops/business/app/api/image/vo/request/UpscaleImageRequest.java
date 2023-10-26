package com.starcloud.ops.business.app.api.image.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-25
 */
@Valid
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UpscaleImageRequest", description = "图片放大基请求")
public class UpscaleImageRequest extends BaseImageRequest {

    private static final long serialVersionUID = 127234507376987176L;
    
    /**
     * 初始化图像
     */
    @Schema(description = "初始化图像")
    private String initImage;

    /**
     * 放大倍数
     */
    @Schema(description = "放大倍数")
    @Min(value = 1, message = "magnification must be greater than or equal to 1")
    @Max(value = 16, message = "magnification must be less than or equal to 16")
    private Integer magnification;

    /**
     * 原始图像的高度（以像素为单位）
     */
    @Schema(description = "原始图像的高度（以像素为单位）")
    private Integer originalHeight;

    /**
     * 原始图像的宽度（以像素为单位）
     */
    @Schema(description = "原始图像的宽度（以像素为单位）")
    private Integer originalWidth;

    /**
     * 图像的高度（以像素为单位）
     */
    @Schema(description = "图像的高度（以像素为单位）。必须以 64 为增量")
    private Integer height;

    /**
     * 图像的宽度（以像素为单位）
     */
    @Schema(description = "图像的宽度（以像素为单位）。必须以 64 为增量")
    private Integer width;

}
