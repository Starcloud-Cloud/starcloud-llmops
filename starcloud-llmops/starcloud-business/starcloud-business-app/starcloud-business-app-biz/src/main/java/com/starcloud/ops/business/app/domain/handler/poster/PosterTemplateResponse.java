package com.starcloud.ops.business.app.domain.handler.poster;

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
public class PosterTemplateResponse implements java.io.Serializable {

    private static final long serialVersionUID = 5957340786508719385L;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;

    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private String errorCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMessage;

    /**
     * 海报图片模板ID
     */
    @Schema(description = "图片模板")
    private String id;

    /**
     * 海报图片模板名称
     */
    @Schema(description = "图片模板名称")
    private String name;

    /**
     * 是否是海报主图
     */
    @Schema(description = "是否是主图")
    private Boolean isMain;

    /**
     * 海报图片序号
     */
    @Schema(description = "图片序号")
    private Integer index;

    /**
     * 海报图片地址
     */
    @Schema(description = "海报图片地址")
    private String url;

}
