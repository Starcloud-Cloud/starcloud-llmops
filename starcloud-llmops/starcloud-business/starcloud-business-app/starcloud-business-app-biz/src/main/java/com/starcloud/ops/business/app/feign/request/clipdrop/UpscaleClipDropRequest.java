package com.starcloud.ops.business.app.feign.request.clipdrop;

import feign.form.FormProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@Schema(name = "UpscaleClipDropRequest", description = "ClipDrop 图片放大请求")
public class UpscaleClipDropRequest extends ClipDropImageRequest {

    private static final long serialVersionUID = -8900599171890482221L;

    /**
     * 需要放大的图片
     */
    @Schema(description = "需要放大的图片")
    @NotNull(message = "需要放大的图片不能为空")
    @FormProperty("image_file")
    private File imageFile;

    /**
     * 需要放大的宽度
     */
    @Schema(description = "需要放大的宽度")
    @Min(value = 1, message = "需要放大的宽度不能小于 1")
    @Max(value = 4096, message = "需要放大的宽度不能大于 4096")
    @FormProperty("target_width")
    private Integer width;

    /**
     * 需要放大的宽高度
     */
    @Schema(description = "需要放大的高度")
    @Min(value = 1, message = "需要放大的高度不能小于 1")
    @Max(value = 4096, message = "需要放大的高度不能大于 4096")
    @FormProperty("target_height")
    private Integer height;

}
