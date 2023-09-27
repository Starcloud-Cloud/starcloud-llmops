package com.starcloud.ops.business.app.feign.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * ClipDrop 图片
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ClipDropImage", description = "ClipDrop 图片")
public class ClipDropImage {

    /**
     * 图片的唯一标识
     */
    @Schema(description = "图片的唯一标识")
    private String uuid;

    /**
     * 图片地址
     */
    @Schema(description = "图片地址")
    private String url;

    /**
     * 图片媒体类型
     */
    @Schema(description = "图片媒体类型")
    @JsonProperty(value = "media_type")
    private String mediaType;

    /**
     * 图片二进制数据流
     */
    @Schema(description = "图片二进制数据流")
    private byte[] binary;

}
