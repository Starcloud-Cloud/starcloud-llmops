package com.starcloud.ops.business.app.feign.request.stability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starcloud.ops.business.app.enums.vsearch.MaskSourceEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-14
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "MaskingStabilityImageRequest", description = "Stability Ai 基于蒙版图片生成图片基础请求")
public class MaskingStabilityImageRequest extends StabilityImageRequest {

    private static final long serialVersionUID = -8473852330307297380L;

    /**
     * 待处理图片二进制数据，base64 数据
     */
    @Schema(description = "待处理图片")
    @NotBlank(message = "initImage can not be blank, please check it.")
    @JsonProperty(value = "init_image")
    private String initImage;

    /**
     * 蒙版源类型
     * <p>
     * MASK_IMAGE_WHITE 将使用mask_image的白色像素作为掩码，其中白色像素被完全替换，黑色像素不变 <br>
     * MASK_IMAGE_BLACK 将使用mask_image的黑色像素作为掩码，其中黑色像素被完全替换，白色像素保持不变 <br>
     * INIT_IMAGE_ALPHA 将使用init_image的 alpha 通道作为掩码，其中完全透明的像素被完全替换，完全不透明的像素保持不变 <br>
     */
    @Schema(description = "蒙版源类型")
    @NotBlank(message = "maskSource can not be blank, please check it.")
    @JsonProperty(value = "mask_source")
    @InEnum(value = MaskSourceEnum.class, field = InEnum.EnumField.NAME, message = "maskSource[{value}] must be in {values}")
    private String maskSource;

    /**
     * 蒙版图片二进制数据，base64 数据
     */
    @Schema(description = "蒙版图片")
    @JsonProperty(value = "mask_image")
    private String maskImage;

}
