package com.starcloud.ops.business.app.feign.request.clipdrop;

import feign.form.FormProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 文字生成图生成请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-08
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "TextToImageClipDropRequest", description = "文字生成图生成请求")
public class TextToImageClipDropRequest extends ClipDropImageRequest {

    private static final long serialVersionUID = -3469843340908373313L;

    /**
     * 生成图片文字描述提示词
     */
    @Schema(description = "生成图片描述提示词")
    @NotBlank(message = "生成图片描述提示词不能为空")
    @Size(max = 1000, message = "生成图片描述提示词不能超过1000个字符")
    @FormProperty("prompt")
    private String prompt;
}
