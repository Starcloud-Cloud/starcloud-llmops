package com.starcloud.ops.business.app.model.poster;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "文案内容对象")
public class PosterTitleDTO implements java.io.Serializable {

    private static final long serialVersionUID = 5621091488785340592L;

    /**
     * 图片标题
     */
    @Schema(description = "图片标题")
    private String imgTitle;

    /**
     * 图片副标题
     */
    @Schema(description = "图片副标题")
    private String imgSubTitle;

    /**
     * 是否使用图片标题
     */
    @Schema(description = "是否使用图片标题")
    private Boolean isUseImgTitle = false;

    /**
     * 是否使用图片副标题
     */
    @Schema(description = "是否使用图片副标题")
    private Boolean isUseImgSubTitle = false;
}
