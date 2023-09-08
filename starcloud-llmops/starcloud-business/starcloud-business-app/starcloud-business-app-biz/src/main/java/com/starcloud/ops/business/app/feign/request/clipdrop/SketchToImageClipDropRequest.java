package com.starcloud.ops.business.app.feign.request.clipdrop;

import feign.form.FormProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 草稿图生成图片请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-08
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "SketchToImageClipDropRequest", description = "草稿图生成图片请求")
public class SketchToImageClipDropRequest extends ClipDropImageRequest {

    private static final long serialVersionUID = -3469843340908373313L;

    /**
     * 需要替换背景的图片
     */
    @Schema(description = "草稿图片")
    @NotNull(message = "草稿图片不能为空")
    @FormProperty("sketch_file")
    private MultipartFile sketchFile;

    /**
     * 草稿生成背景描述提示词
     */
    @Schema(description = "生成图片描述提示词")
    @NotBlank(message = "生成图片描述提示词不能为空")
    @Size(max = 5000, message = "生成图片描述提示词不能超过5000个字符")
    @FormProperty("prompt")
    private String prompt;
}
