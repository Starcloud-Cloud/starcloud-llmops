package com.starcloud.ops.business.app.api.image.vo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-11
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "VariantsImageResponse", description = "图像信息实体")
public class VariantsImageResponse extends BaseImageResponse {

    private static final long serialVersionUID = 6679789337262231708L;

    /**
     * 原始图片的 URL
     */
    @Schema(description = "原始图片的 URL")
    private String originalUrl;

    /**
     * 图片的Engine
     */
    @Schema(description = "请求的 Engine ")
    private String engine;
    
    /**
     * 请求的 Prompt
     */
    @Schema(description = "请求的 Prompt ")
    private String prompt;

    /**
     * 反义词
     */
    @Schema(description = "反义词")
    private String negativePrompt;

    /**
     * 图片的宽度
     */
    @Schema(description = "图片的宽度")
    private Integer width;

    /**
     * 图片的高度
     */
    @Schema(description = "图片的高度")
    private Integer height;

    /**
     * 图片的步骤
     */
    @Schema(description = "图片的步骤")
    private Integer steps;

    /**
     * 图片的类型
     */
    @Schema(description = "图片的类型")
    private String stylePreset;
}
