package com.starcloud.ops.business.app.api.image.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图片生成应用实体")
public class ImageReqVO implements Serializable {

    private static final long serialVersionUID = 1317039328445443438L;

    @Schema(description = "会话id")
    private String conversationId;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 图片生成参数配置
     */
    @Valid
    private ImageRequest imageRequest;

}
