package com.starcloud.ops.business.app.api.image.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@Schema(name = "SketchToImageResponse", description = "草图生成图片响应")
public class SketchToImageResponse extends BaseImageResponse {

    private static final long serialVersionUID = -4416869005531265924L;

    /**
     * 草图
     */
    @Schema(description = "草稿图片")
    @NotNull(message = "草稿图片不能为空")
    private String originalUrl;

    /**
     * 草稿生成背景描述提示词
     */
    @Schema(description = "生成图片描述提示词")
    @NotBlank(message = "生成图片描述提示词不能为空")
    @Size(max = 5000, message = "生成图片描述提示词不能超过5000个字符")
    private String prompt;

}
