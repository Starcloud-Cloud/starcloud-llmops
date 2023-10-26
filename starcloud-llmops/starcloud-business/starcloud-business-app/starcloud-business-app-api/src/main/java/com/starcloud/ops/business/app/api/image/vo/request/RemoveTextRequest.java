package com.starcloud.ops.business.app.api.image.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-21
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "RemoveTextRequest", description = "去除图片文字请求")
public class RemoveTextRequest extends BaseImageRequest {

    private static final long serialVersionUID = 2758177754918933511L;

    /**
     * 需要处理的背景图片的 URL
     */
    @Schema(description = "需要处理的图片的URL")
    @NotEmpty(message = "图片地址不能为空")
    private String imageUrl;

}
