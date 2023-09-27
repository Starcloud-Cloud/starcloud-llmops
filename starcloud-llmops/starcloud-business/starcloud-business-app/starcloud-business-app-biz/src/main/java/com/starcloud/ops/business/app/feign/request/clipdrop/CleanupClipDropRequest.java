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
 * ClipDrop 图片放大请求
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-07
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "CleanupClipDropRequest", description = "ClipDrop 图片修复请求")
public class CleanupClipDropRequest extends ClipDropImageRequest {

    private static final long serialVersionUID = -3413366456317986833L;

    /**
     * 需要放大的图片
     */
    @Schema(description = "需要修复的图片")
    @NotNull(message = "需要修复的图片不能为空")
    @FormProperty("image_file")
    private File imageFile;

    /**
     * 需要放大的图片
     */
    @Schema(description = "需要修复的遮罩图片")
    @NotNull(message = "需要修复的遮罩图片不能为空")
    @FormProperty("mask_file")
    private File maskFile;
}
