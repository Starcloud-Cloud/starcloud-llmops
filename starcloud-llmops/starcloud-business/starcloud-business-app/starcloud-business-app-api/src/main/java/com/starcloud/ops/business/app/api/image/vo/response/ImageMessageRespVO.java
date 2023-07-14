package com.starcloud.ops.business.app.api.image.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @Schema(description = "请求的 Prompt ")
    private String prompt;

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
