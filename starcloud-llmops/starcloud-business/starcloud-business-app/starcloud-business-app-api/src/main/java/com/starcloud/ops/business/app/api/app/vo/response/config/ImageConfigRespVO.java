package com.starcloud.ops.business.app.api.app.vo.response.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.image.dto.TextPrompt;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * 图片生成配置
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图片生成配置")
public class ImageConfigRespVO extends BaseConfigRespVO {

    private static final long serialVersionUID = -4540655599582546170L;

    /**
     * 引擎
     */
    @Schema(description = "引擎")
    private String engine;

    /**
     * 用于生成的文本提示集合。
     */
    @Schema(description = "用于生成的文本提示集合")
    private List<TextPrompt> prompts;

    /**
     * 初始化图像
     */
    @Schema(description = "初始化图像")
    private String initImage;

    /**
     * 遮罩图像
     */
    @Schema(description = "遮罩图像")
    private String maskImage;

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
     * 跳过扩散步骤开始的一部分，允许init_image影响最终生成的图像。较低的值将导致init_image的影响更大，而较高的值将导致扩散步骤的影响更大
     */
    @Schema(description = "跳过扩散步骤开始的一部分，允许init_image影响最终生成的图像。较低的值将导致init_image的影响更大，而较高的值将导致扩散步骤的影响更大")
    private Double startSchedule;

    /**
     * 跳过扩散步骤结束的一部分，允许init_image影响最终生成的图像。较低的值将导致init_image的影响更大，而较高的值将导致扩散步骤的影响更大
     */
    @Schema(description = "跳过扩散步骤结束的一部分，允许 init_image 影响最终生成的图像。较低的值将导致init_image的影响更大，而较高的值将导致扩散步骤的影响更大")
    private Double endSchedule;

    /**
     * 扩散过程遵循提示文本的严格程度（值越高，图像越靠近提示）。
     */
    @Schema(description = "扩散过程遵循提示文本的严格程度（值越高，图像越靠近提示）")
    private Double cfgScale;

    /**
     * 用于扩散过程的采样器。如果省略此值，我们将自动为您选择合适的采样器
     */
    @Schema(description = "用于扩散过程的采样器。如果省略此值，我们将自动为您选择合适的采样器")
    private Integer sampler;

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

    /**
     * 要生成的图像数
     */
    @Schema(description = "要生成的图像数")
    private Integer samples;

    /**
     * 剪辑指南预设
     */
    @Schema(description = "剪辑指南预设")
    private Integer guidancePreset;

    /**
     * 用于指导的切割数
     */
    @Schema(description = "用于指导的切割数")
    private Integer guidanceCuts;

    /**
     * 指导强度。我们建议取值范围为[0.0,1.0]。最好的默认值是0.25
     */
    @Schema(description = "指导强度。我们建议取值范围为[0.0,1.0]。最好的默认值是0.25")
    private Double guidanceStrength;

    /**
     * 指导图像的 prompt，如果未指定，默认为 prompt 参数(如上)
     */
    @Schema(description = "指导图像的 prompt，如果未指定，默认为 prompt 参数")
    private String guidancePrompt;

    /**
     * 用于指导的模型
     */
    @Schema(description = "用于指导的模型")
    private List<String> guidanceModels;

    /**
     * 传入样式预设以引导图像模型走向特定样式。 此样式预设列表可能会更改
     */
    @Schema(description = "传入样式预设以引导图像模型走向特定样式。 此样式预设列表可能会更改")
    private String stylePreset;

}
