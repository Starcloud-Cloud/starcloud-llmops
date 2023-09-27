package com.starcloud.ops.business.app.api.image.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.enums.vsearch.GuidancePresetEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplerEnum;
import com.starcloud.ops.business.app.enums.vsearch.StylePresetEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 根据文本生成图片基础请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Valid
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "GenerateImageRequest", description = "生成图片请求(文生图，图生图)")
public class GenerateImageRequest extends BaseImageRequest {

    private static final long serialVersionUID = 7257321428822990334L;

    /**
     * 引擎
     */
    @Schema(description = "引擎")
    private String engine;

    /**
     * 用于生成的文本提示集合。
     */
    @Schema(description = "用于生成的文本提示集合")
    @NotBlank(message = "prompt can not be blank, please check it.")
    private String prompt;

    /**
     * 反义词
     */
    @Schema(description = "反义词")
    private String negativePrompt;

    /**
     * 图片生成图片的基础图像
     */
    @Schema(description = "图片生成图片的基础图像")
    private String initImage;

    /**
     * 图像的高度（以像素为单位）。必须以 64 为增量，并通过以下验证
     * 对于 768 引擎：589,824 ≤ height * width ≤ 1,048,576
     * 所有其他引擎：262,144 ≤ height * width ≤ 1,048,576
     */
    @Schema(description = "图像的高度（以像素为单位）。必须以 64 为增量")
    private Integer height;

    /**
     * 图像的宽度（以像素为单位）。必须以 64 为增量，并通过以下验证：
     * 对于 768 引擎：589,824 ≤ height * width ≤ 1,048,576
     * 所有其他引擎：262,144 ≤ height * width ≤ 1,048,576
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
     * 用于扩散过程的采样器。如果省略此值，我们将自动为您选择合适的采样器
     */
    @Schema(description = "用于扩散过程的采样器。如果省略此值，我们将自动为您选择合适的采样器")
    @InEnum(value = SamplerEnum.class, field = InEnum.EnumField.CODE, message = "sampler[{value}] must be in {values}")
    private Integer sampler;

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
    @Max(value = 4294967295L, message = "seed must be less than or equal to 4294967295")
    private Long seed;

    /**
     * 要生成的图像数
     */
    @Schema(description = "要生成的图像数")
    @Min(value = 1, message = "samples must be greater than or equal to 1")
    @Max(value = 10, message = "samples must be less than or equal to 10")
    private Integer samples;

    /**
     * 剪辑指南预设
     */
    @Schema(description = "剪辑指南预设")
    @InEnum(value = GuidancePresetEnum.class, field = InEnum.EnumField.CODE, message = "guidancePreset[{value}] must be in {values}")
    private Integer guidancePreset;

    /**
     * 传入样式预设以引导图像模型走向特定样式。 此样式预设列表可能会更改
     */
    @Schema(description = "传入样式预设以引导图像模型走向特定样式。 此样式预设列表可能会更改")
    @InEnum(value = StylePresetEnum.class, field = InEnum.EnumField.CODE, message = "stylePreset[{value}] must be in {values}")
    private String stylePreset;

    /**
     * This parameter is just an alternate way to set step_schedule_start, which is done via the calculation 1 - image_strength. For example, passing in an Image Strength of 35% (0.35) would result in a step_schedule_start of 0.65.
     */
    @Schema(description = "这个参数只是设置step_schedule_start的另一种方法，它是通过计算1 - image_strength来完成的。例如，传入35%的Image Strength(0.35)将导致step_schedule_start为0.65。")
    @Min(value = 0, message = "imageStrength must be greater than or equal to 0")
    @Max(value = 1, message = "imageStrength must be less than or equal to 1")
    private Double imageStrength;

    /**
     * 指导强度。我们建议取值范围为[0.0,1.0]。最好默认值是0.25
     */
    @Schema(description = "指导强度。我们建议取值范围为[0.0,1.0]。最好的默认值是0.25")
    private Double guidanceStrength;
}
