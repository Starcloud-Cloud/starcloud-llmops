package com.starcloud.ops.business.app.api.image.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-21
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "RemoveBackgroundResponse", description = "去除图片背景响应")
public class RemoveBackgroundResponse extends BaseImageResponse {

    private static final long serialVersionUID = 4464670336015906952L;

    /**
     * 需要处理的背景图片的 URL
     */
    @Schema(description = "需要处理的图片的URL")
    private String originalUrl;

}
