package com.starcloud.ops.business.app.api.image.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UpscaleImageResponse", description = "图片放大响应")
public class UpscaleImageResponse extends BaseImageResponse {

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
    @Schema(description = "原来的图像")
    private String originalUrl;

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
     * 放大倍数
     */
    @Schema(description = "放大倍数")
    private Integer magnification;

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
     * 要运行的扩散步骤数
     */
    @Schema(description = "要运行的扩散步骤数")
    private Integer steps;

    /**
     * 随机噪声种子（省略此选项或用于随机种子)
     */
    @Schema(description = "随机噪声种子（省略此选项或用于随机种子)")
    private Long seed;

}
