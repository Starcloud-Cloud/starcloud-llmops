package com.starcloud.ops.business.app.api.xhs.scheme.dto;

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
public class CopyWritingContentDTO implements java.io.Serializable {

    private static final long serialVersionUID = -8566370798934412863L;

    /**
     * 文案标题
     */
    @Schema(description = "文案标题")
    private String title;

    /**
     * 文案内容
     */
    @Schema(description = "文案内容")
    private String content;

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


}
