package com.starcloud.ops.business.app.controller.admin.image.vo.task;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * Base Request
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-04-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "StabilityRequest", description = "Stability Ai 基础请求")
public class StabilityImageR implements Serializable {

    private static final long serialVersionUID = 5346767232312319994L;

    /**
     * 引擎
     */
    @Schema(description = "引擎")
    private String engine;

    /**
     * 用于生成的文本提示集合。
     */
    @Schema(description = "用于生成的文本提示集合")
    private String prompt;

    /**
     * 初始化图像
     */
    @Schema(description = "初始化图像")
    private MultipartFile initImage;

    /**
     * 遮罩图像
     */
    @Schema(description = "遮罩图像")
    private MultipartFile maskImage;

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
    @Min(value = 0, message = "start_schedule must be greater than or equal to 0")
    @Max(value = 1, message = "start_schedule must be less than or equal to 1")
    private Double startSchedule;


    /**
     * 扩散过程遵循提示文本的严格程度（值越高，图像越靠近提示）。
     */
    @Schema(description = "扩散过程遵循提示文本的严格程度（值越高，图像越靠近提示）")
    @Min(value = 0, message = "cfgScale must be greater than or equal to 0")
    @Max(value = 35, message = "cfgScale must be less than or equal to 35")
    private Integer cfgScale;

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
    @Max(value = 2147483647, message = "seed must be less than or equal to 4294967295")
    private Long seed;

    /**
     * 要生成的图像数
     */
    @Schema(description = "要生成的图像数")
    @Min(value = 1, message = "samples must be greater than or equal to 1")
    @Max(value = 10, message = "samples must be less than or equal to 10")
    private Integer samples;


}
