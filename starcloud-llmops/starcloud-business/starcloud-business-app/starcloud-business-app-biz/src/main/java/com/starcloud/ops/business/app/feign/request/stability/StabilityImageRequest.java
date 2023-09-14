package com.starcloud.ops.business.app.feign.request.stability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starcloud.ops.business.app.api.image.dto.TextPrompt;
import com.starcloud.ops.business.app.enums.vsearch.GuidancePresetEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplerEnum;
import com.starcloud.ops.business.app.enums.vsearch.StylePresetEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-14
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "StabilityImageRequest", description = "Stability Ai 图片基础请求")
public class StabilityImageRequest implements Serializable {

    private static final long serialVersionUID = 720755835344190325L;

    /**
     * 提示词集合
     */
    @Schema(description = "提示词集合")
    @NotEmpty(message = "textPrompts can not be empty, please check it.")
    @JsonProperty(value = "text_prompts")
    private List<TextPrompt> textPrompts;

    /**
     * 扩散过程遵循提示文本的严格程度(数值越高，图像越接近提示)
     */
    @Schema(description = "扩散过程遵循提示文本的严格程度(数值越高，图像越接近提示)")
    @Min(value = 0, message = "cfgScale must be greater than or equal to 1")
    @Max(value = 35, message = "cfgScale must be less than or equal to 35")
    @JsonProperty(value = "cfg_scale")
    private Integer cfgScale;

    /**
     * 预设剪辑指导
     */
    @Schema(description = "预设剪辑指导")
    @JsonProperty(value = "clip_guidance_preset")
    @InEnum(value = GuidancePresetEnum.class, field = InEnum.EnumField.NAME, message = "clipGuidancePreset[{value}] must be in {values}")
    private String clipGuidancePreset;

    /**
     * 扩散过程中使用哪种取样器。如果省略此值，我们将自动为您选择合适的采样器。
     */
    @Schema(description = "扩散过程中使用哪种取样器。如果省略此值，我们将自动为您选择合适的采样器")
    @JsonProperty(value = "sampler")
    @InEnum(value = SamplerEnum.class, field = InEnum.EnumField.NAME, message = "sampler[{value}] must be in {values}")
    private String sampler;

    /**
     * 生成图片数量
     */
    @Schema(description = "生成图片数量")
    @JsonProperty(value = "samples")
    @Min(value = 1, message = "samples must be greater than or equal to 1.")
    @Max(value = 10, message = "samples must be less than or equal to 10.")
    private Integer samples;

    /**
     * 随机噪声种子(省略此选项或使用0作为随机种子)
     */
    @Schema(description = "随机噪声种子(省略此选项或使用0作为随机种子)")
    @JsonProperty(value = "seed")
    private Long seed;

    /**
     * 要运行的扩散步骤数, 默认50
     */
    @Schema(description = "要运行的扩散步骤数")
    @JsonProperty(value = "steps")
    private Integer steps;

    /**
     * 样式预设 <br>
     * 传入一个样式预设来引导图像模型朝向一个特定的样式。此样式预设列表可能会更改。
     */
    @Schema(description = "样式预设")
    @JsonProperty(value = "style_preset")
    @InEnum(value = StylePresetEnum.class, field = InEnum.EnumField.CODE, message = "stylePreset[{value}] must be in {values}")
    private String stylePreset;

}
