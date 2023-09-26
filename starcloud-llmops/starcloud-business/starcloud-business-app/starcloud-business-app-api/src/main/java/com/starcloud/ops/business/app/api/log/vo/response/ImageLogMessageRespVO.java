package com.starcloud.ops.business.app.api.log.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.starcloud.ops.business.app.api.image.vo.response.BaseImageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "图片日志详情基础实体")
public class ImageLogMessageRespVO extends LogMessageDetailRespVO {

    private static final long serialVersionUID = 4508753413698097535L;

    /**
     * 图片信息
     */
    @Schema(description = "图片信息")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "fromScene")
    private BaseImageResponse imageInfo;
}
