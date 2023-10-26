package com.starcloud.ops.business.app.api.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-21
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "UploadImageInfoDTO", description = "上传图片信息 DTO")
public class UploadImageInfoDTO implements Serializable {

    private static final long serialVersionUID = -6590350286772382043L;

    /**
     * 图片的唯一标识
     */
    @Schema(description = "图片的唯一标识")
    private String uuid;

    /**
     * 图片名称
     */
    @Schema(description = "图片名称")
    private String name;

    /**
     * 图片上传时候名称
     */
    @Schema(description = "图片上传时候名称")
    private String originalFilename;

    /**
     * 图片媒体类型
     */
    @Schema(description = "图片媒体类型")
    private String mediaType;

    /**
     * 图片地址
     */
    @Schema(description = "图片地址")
    private String url;

    /**
     * 图片宽度
     */
    @Schema(description = "图片宽度")
    private Integer width;

    /**
     * 图片高度
     */
    @Schema(description = "图片高度")
    private Integer height;

}
