package com.starcloud.ops.business.app.feign.request.clipdrop;

import feign.form.FormProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * 只有 imageFile 的请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-08
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "ImageFileClipDropRequest", description = "只有 imageFile 的请求")
public class ImageFileClipDropRequest extends ClipDropImageRequest {

    private static final long serialVersionUID = -8137537946147433486L;

    /**
     * 需要处理的图片
     */
    @Schema(description = "需要处理的图片")
    @NotNull(message = "需要处理的图片不能为空")
    @FormProperty("image_file")
    private File imageFile;

}
