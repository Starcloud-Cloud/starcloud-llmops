package com.starcloud.ops.business.app.api.image.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "BaseImageRequest", description = "图片生成基础请求")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "scene")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GenerateImageRequest.class, name = "WEB_IMAGE"),
        @JsonSubTypes.Type(value = RemoveBackgroundRequest.class, name = "IMAGE_REMOVE_BACKGROUND"),
        @JsonSubTypes.Type(value = RemoveTextRequest.class, name = "IMAGE_REMOVE_TEXT"),
        @JsonSubTypes.Type(value = SketchToImageRequest.class, name = "IMAGE_SKETCH"),
        @JsonSubTypes.Type(value = UpscaleImageRequest.class, name = "IMAGE_UPSCALING"),
        @JsonSubTypes.Type(value = VariantsImageRequest.class, name = "IMAGE_VARIANTS"),
})
public class BaseImageRequest implements Serializable {

    private static final long serialVersionUID = -5821971856070635605L;

}
