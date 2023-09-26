package com.starcloud.ops.business.app.feign.request.clipdrop;

import feign.form.FormProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * ClipDrop 替换背景请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-08
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "ReplaceBackgroundClipDropRequest", description = "ClipDrop 图片替换背景请求")
public class ReplaceBackgroundClipDropRequest extends ClipDropImageRequest {

    private static final long serialVersionUID = -555274940919904821L;

    /**
     * 需要替换背景的图片
     */
    @Schema(description = "需要替换背景的图片")
    @NotNull(message = "需要替换背景的图片不能为空")
    @FormProperty("image_file")
    private File imageFile;

    /**
     * 生成背景描述提示词
     */
    @Schema(description = "生成背景描述提示词")
    @NotBlank(message = "生成背景描述提示词不能为空")
    @FormProperty("prompt")
    private String prompt;
}
