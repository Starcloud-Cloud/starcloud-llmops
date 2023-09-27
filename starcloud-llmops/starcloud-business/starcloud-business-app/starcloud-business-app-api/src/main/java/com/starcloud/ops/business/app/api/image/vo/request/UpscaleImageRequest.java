package com.starcloud.ops.business.app.api.image.vo.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
     * 引擎
     */
    @Schema(description = "引擎")
    private String engine;

    /**
     * 提示词
     */
    @Schema(description = "提示词")
    private String prompt;

    /**
     * 初始化图像
     */
    @Schema(description = "初始化图像")
    private String initImage;

    /**
     * 初始化图像二进制
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private byte[] initImageBinary;

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

    /**
     * 扩散过程遵循提示文本的严格程度（值越高，图像越靠近提示）。
     */
    @Schema(description = "扩散过程遵循提示文本的严格程度（值越高，图像越靠近提示）")
    @Min(value = 0, message = "cfgScale must be greater than or equal to 0")
    @Max(value = 35, message = "cfgScale must be less than or equal to 35")
    private Double cfgScale;

    /**
     * 要运行的扩散步骤数
     */
    @Schema(description = "要运行的扩散步骤数")
    @Min(value = 10, message = "steps must be greater than or equal to 10")
    @Max(value = 150, message = "steps must be less than or equal to 150")
    private Integer steps;

    /**
     * 随机噪声种子（省略此选项或用于随机种子)
     */
    @Schema(description = "随机噪声种子（省略此选项或用于随机种子)")
    @Min(value = 0, message = "seed must be greater than or equal to 0")
    @Max(value = 2147483647, message = "seed must be less than or equal to 4294967295")
    private Long seed;

}
