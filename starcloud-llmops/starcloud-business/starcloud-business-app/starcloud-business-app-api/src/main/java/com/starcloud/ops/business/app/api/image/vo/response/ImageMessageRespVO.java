package com.starcloud.ops.business.app.api.image.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图像信息实体")
public class ImageMessageRespVO implements Serializable {

    private static final long serialVersionUID = -5639623586957890335L;

    /**
     * 请求的 Prompt
     */
    @Schema(description = "请求的 Prompt ")
    private String prompt;

    /**
     * 反义词
     */
    @Schema(description = "反义词")
    @JsonProperty(value = "negative_prompt")
    private String negativePrompt;

    /**
     * 图片的Engine
     */
    @Schema(description = "请求的 Engine ")
    private String engine;

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
     * 生成的图片时间
     */
    @Schema(description = "生成的图片时间")
    private LocalDateTime createTime;

    /**
     * 生成的图片列表
     */
    @Schema(description = "图片列表")
    private List<ImageDTO> images;
}
