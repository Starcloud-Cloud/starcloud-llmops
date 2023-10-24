package com.starcloud.ops.business.app.api.image.vo.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-25
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "BaseImageResponse", description = "图片响应基类")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "fromScene")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GenerateImageResponse.class, name = "WEB_IMAGE"),
        @JsonSubTypes.Type(value = RemoveBackgroundResponse.class, name = "IMAGE_REMOVE_BACKGROUND"),
        @JsonSubTypes.Type(value = RemoveTextResponse.class, name = "IMAGE_REMOVE_TEXT"),
        @JsonSubTypes.Type(value = SketchToImageResponse.class, name = "IMAGE_SKETCH"),
        @JsonSubTypes.Type(value = UpscaleImageResponse.class, name = "IMAGE_UPSCALING"),
        @JsonSubTypes.Type(value = VariantsImageResponse.class, name = "IMAGE_VARIANTS"),
})
public class BaseImageResponse implements Serializable {

    private static final long serialVersionUID = 3162145745607862874L;

    /**
     * 场景
     */
    @Schema(description = "场景")
    private String fromScene;

    /**
     * 生成的图片列表
     */
    @Schema(description = "图片列表")
    private List<ImageDTO> images;

    /**
     * 生成时间
     */
    @Schema(description = "生成时间")
    private Date generateTime = new Date();

    /**
     * 构造空的响应
     *
     * @param fromScene 场景
     * @return 空的响应
     */
    public static BaseImageResponse ofEmpty(String fromScene) {
        BaseImageResponse response = new BaseImageResponse();
        response.setFromScene(fromScene);
        response.setGenerateTime(new Date());
        response.setImages(Collections.emptyList());
        return response;
    }
}
