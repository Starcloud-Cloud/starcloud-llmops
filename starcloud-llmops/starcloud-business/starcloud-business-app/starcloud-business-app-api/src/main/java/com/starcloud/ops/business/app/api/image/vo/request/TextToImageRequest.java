package com.starcloud.ops.business.app.api.image.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Valid
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TextToImageRequest", description = "根据文本生成图片请求")
public class TextToImageRequest extends ImageRequest {

    private static final long serialVersionUID = 5716974918556637581L;


}
