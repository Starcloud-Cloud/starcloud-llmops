package com.starcloud.ops.business.app.api.scheme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "ReferenceImageDTO", description = "参考图片")
public class ReferenceImageDTO implements java.io.Serializable {

    private static final long serialVersionUID = -8405515771282223566L;

    /**
     * 图片URL
     */
    @Schema(description = "图片URL")
    @NotBlank(message = "参考图片：图片URL不能为空！")
    private String url;

    /**
     * 图片参考文本
     */
    @Schema(description = "图片标题")
    private String title;

    /**
     * 图片参考文本
     */
    @Schema(description = "图片副标题")
    private String subTitle;
}
